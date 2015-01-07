/**
 * 
 */
package com.icloudobject.topo.resource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.ebay.cloud.cms.dal.entity.IEntity;
import com.ebay.cloud.cms.query.service.IQueryResult;
import com.ebay.cloud.cms.query.service.QueryContext;
import com.ebay.cloud.cms.sysmgmt.priority.CMSPriority;
import com.ebay.cloud.cms.sysmgmt.server.CMSServer;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @author Liangfei
 * 
 */
@Path("/repositories/{reponame}/branches/{branch}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EntityResource extends
        com.ebay.cloud.cms.service.resources.impl.EntityResource {

    private static final String TOPO_CLIENT_ID = "topoClientId";

    private String getTopoClientId(HttpServletRequest request) {
        try {
            return request.getParameter(TOPO_CLIENT_ID);
        } catch (Exception e) {
            return null;
        }
    }

    @POST
    @Path("/topo/{metadata}")
    public Response topoPost(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request) {
        String topoClientId = getTopoClientId(request);
        if (topoClientId == null) {
            throw (new IllegalArgumentException("topoClientId cannot be null"));
        }
        JSONArray jsonArray = (JSONArray) JSONValue.parse(jsonString);
        List<String> responses = new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            String jsonStr = jsonArray.get(i).toString();
            responses.add(handleOneTopo(uriInfo, reponame, branch, metadata,
                    priority, consistPolicy, jsonStr, modeVal, request,
                    topoClientId));
        }
        return Response.ok(responses).build();
    }

    private String handleOneTopo(UriInfo uriInfo, final String reponame,
            final String branch, final String metadata, final String priority,
            final String consistPolicy, String jsonString, String modeVal,
            HttpServletRequest request, String topoClientId) {
        BasicDBObject config = TopoConfigCache
                .getConfig(topoClientId, metadata);
        String className = config.getString("className");
        BasicDBObject fields = (BasicDBObject) config.get("fields");
        String objectId = null;
        JSONObject payload = new JSONObject();
        for (Entry<String, Object> entry : fields.entrySet()) {
            String attrName = entry.getKey();

            BasicDBObject attrDef = (BasicDBObject) entry.getValue();
            String refClassName = attrDef.getString("class");
            String dataType = attrDef.getString("dataType");
            Object pathObj = attrDef.get("path");
            Object value = null;
            if (attrName.equals("id")) {
                BasicDBList paths = (BasicDBList) pathObj;
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < paths.size(); i++) {
                    try {
                        String v1 = JsonPath.read(jsonString, (String) paths.get(i));
                        if (sb.length() == 0) {
                            sb.append(v1);
                        } else {
                            sb.append(":").append(v1);
                        }
                    } catch (Exception e) {
                        // pass
                    }
                }
                value = sb.toString();
            } else {
                try {
                    value = JsonPath.read(jsonString, (String) pathObj);
                } catch (Exception e) {
                    
                }
            }
            
            if (value == null) {
                System.out.println("cannot find value for path:" + pathObj + " from json:" + jsonString);
            }
            if (attrName.equals("id")) {
                if (value == null) {
                    return "noIdFound";
                }
                objectId = value.toString();
            }
            if (refClassName != null) {
                JSONObject refObj = new JSONObject();
                refObj.put("_oid", value);
                refObj.put("_type", refClassName);
                payload.put(attrName, refObj);
                if (!existObject(reponame, refClassName, value.toString())) {
                    String postStr = "{\"_oid\":\"" + value + "\"}";
                    try {
                        super.createEntity(uriInfo, reponame, branch,
                                refClassName, priority, consistPolicy, postStr,
                                modeVal, request);
                    } catch (Exception e) {
                        return "Error when create " + refClassName + " for "
                                + value + " message is:" + e.getMessage();
                    }
                }
            } else if (dataType!= null && dataType.equals("date")) {
                long time = Long.parseLong(value.toString());
                payload.put(attrName, time);   
            } else {
                payload.put(attrName, value);
            }
        }
        if (objectId == null) {
            return "objectIdIsNull";
        }
        String status = "FAIL";
        if (existObject(reponame, className, objectId)) {
            try {
                Response r = super.modifyEntity(uriInfo, reponame, branch,
                        metadata, objectId, priority, consistPolicy,
                        payload.toString(), modeVal, request);
                if (r.getStatus() == 200) {
                    status = objectId + " UPDATED_OK";
                } else {
                    status = objectId + " UPDATE_FAIL";
                }
            } catch (Exception e) {
                status = objectId + " UpdateException:" + e.getMessage();
            }
        } else {
            payload.put("_oid", objectId);
            try {
                Response r = super.createEntity(uriInfo, reponame, branch,
                        className, priority, consistPolicy, payload.toString(),
                        modeVal, request);
                if (r.getStatus() == 200) {
                    status = objectId + " INSERTED_OK";
                } else {
                    status = objectId + " INSERT_FAIL";
                }
            } catch (Exception e) {
                status = objectId + " InsertException:" + e.getMessage();

            }
        }
        return status;
    }

    private boolean existObject(String repo, String className, String id) {
        String queryString = className + "[@_oid=\"" + id + "\"]";
        QueryContext queryContext = new QueryContext(repo, "main");
        queryContext.setAllowFullTableScan(true);
        IQueryResult result = CMSServer.getCMSServer().query(
                CMSPriority.NEUTRAL, queryString, queryContext);
        List<IEntity> entities = result.getEntities();
        if (entities == null || entities.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @GET
    @Path("/topo/{metadata}")
    public String topoTest(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request) {
        return "Topo API ready";
    }

}
