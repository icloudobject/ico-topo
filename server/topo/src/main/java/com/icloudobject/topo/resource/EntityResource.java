/**
 * 
 */
package com.icloudobject.topo.resource;

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

/**
 * @author Liangfei
 * 
 */
@Path("/repositories/{reponame}/branches/{branch}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EntityResource extends
        com.ebay.cloud.cms.service.resources.impl.EntityResource {

    private static final String SERVICE_CLIENT_ID = "topoClientId";

    private String getTopoClientId(HttpServletRequest request) {
        try {
            return request.getParameter(SERVICE_CLIENT_ID);
        } catch (Exception e) {
            return null;
        }
    }

    private static String convertTopo(String clientId, String reponame,
            String jsonString) {
        return jsonString;
    }

    @POST
    @Path("/{metadata}")
    public Response createEntity(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request) {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }
        return super.createEntity(uriInfo, reponame, branch, metadata,
                priority, consistPolicy, jsonString, modeVal, request);
    }

    @PUT
    @Path("/{metadata}/{oid}")
    public Response replaceEntity(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @PathParam("oid") final String oid,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request) {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }
        return super.replaceEntity(uriInfo, reponame, branch, metadata, oid,
                priority, consistPolicy, jsonString, modeVal, request);
    }

    @POST
    @Path("/{metadata}/{oid}")
    public Response modifyEntity(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @PathParam("oid") final String oid,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request) {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }
        return super.modifyEntity(uriInfo, reponame, branch, metadata, oid,
                priority, consistPolicy, jsonString, modeVal, request);
    }

    @POST
    @Path("/entities")
    public Response batchCreateEntities(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @QueryParam("failReturnOption") final String failReturnOptionVal,
            @Context HttpServletRequest request) {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }

        return super.batchCreateEntities(uriInfo, reponame, branch, priority,
                consistPolicy, jsonString, modeVal, failReturnOptionVal,
                request);
    }

    @PUT
    @Path("/entities")
    public Response batchModifyEntities(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @QueryParam("failReturnOption") final String failReturnOptionVal,
            @Context HttpServletRequest request) {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }

        return super.batchModifyEntities(uriInfo, reponame, branch, priority,
                consistPolicy, jsonString, modeVal, failReturnOptionVal,
                request);
    }

    @POST
    @Path("/{metadata}/{oid}/{fieldname}")
    @Override
    public Response modifyEntityField(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @PathParam("oid") final String oid,
            @PathParam("fieldname") final String fieldName,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            @HeaderParam("X-CMS-CONDITIONAL-UPDATE") final String casMode,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request) {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }
        return super.modifyEntityField(uriInfo, reponame, branch, metadata,
                oid, fieldName, priority, consistPolicy, casMode, jsonString,
                modeVal, request);
    }
    
    @POST
    @Path("/topo/{metadata}")
    public Response topoCreate(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request)
    {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }
        return super.createEntity(uriInfo, reponame, branch, metadata,
                priority, consistPolicy, jsonString, modeVal, request);
    }
    
    @PUT
    @Path("/topo/{metadata}")
    public Response topoUpdate(@Context final UriInfo uriInfo,
            @PathParam("reponame") final String reponame,
            @PathParam("branch") final String branch,
            @PathParam("metadata") final String metadata,
            @HeaderParam("X-CMS-PRIORITY") final String priority,
            @HeaderParam("X-CMS-CONSISTENCY") final String consistPolicy,
            String jsonString, @QueryParam("mode") String modeVal,
            @Context HttpServletRequest request)
    {
        String topoClientId = getTopoClientId(request);
        if (topoClientId != null) {
            jsonString = convertTopo(topoClientId, reponame, jsonString);
        }
        //TODO : topo update 
        return null;
//        return super.up(uriInfo, reponame, branch, metadata,
//                priority, consistPolicy, jsonString, modeVal, request);
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
            @Context HttpServletRequest request)
    {
        return "Topo API ready";
    }

}
