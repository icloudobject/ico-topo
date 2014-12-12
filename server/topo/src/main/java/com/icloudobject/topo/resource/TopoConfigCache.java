package com.icloudobject.topo.resource;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minidev.json.JSONObject;

import com.ebay.cloud.cms.dal.entity.IEntity;
import com.ebay.cloud.cms.query.service.IQueryResult;
import com.ebay.cloud.cms.query.service.QueryContext;
import com.ebay.cloud.cms.sysmgmt.priority.CMSPriority;
import com.ebay.cloud.cms.sysmgmt.server.CMSServer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.BasicDBObject;

public class TopoConfigCache {
    public static final String TOPO_CONFIG_REPO = "topoconfig";
    public static final String TOPO_CONFIG_MAPPING = "TopoMapping";

    private static LoadingCache<String, BasicDBObject> config = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.HOURS)
            .build(
                new CacheLoader<String, BasicDBObject>() {
                  public BasicDBObject load(String key) throws Exception {
                    String [] keys = key.split(":");
                    String topoClientId = keys[0];
                    String className = keys[1];
                    return getConfigFromCMS(topoClientId, className);
                  }
                });
    
    private static BasicDBObject getConfigFromCMS(String topoClientId, String className) {
        String queryString = TOPO_CONFIG_MAPPING + "[@clientId=\"" + topoClientId 
                + "\" and @className=\"" + className + "\"]{@mapping}";
        QueryContext queryContext = new QueryContext(TOPO_CONFIG_REPO, "main");
        queryContext.setAllowFullTableScan(true);
        IQueryResult result = CMSServer.getCMSServer().query(
                CMSPriority.NEUTRAL, queryString, queryContext);
        List<IEntity> entities = result.getEntities();
        if (entities == null || entities.size() == 0) {
            return null;
        } else {
            return (BasicDBObject) entities.get(0).getFieldValues("mapping").get(0);
        }
        
    }
    public static BasicDBObject getConfig(String topoClientId, String className) {
        try {
            return config.get(topoClientId + ":" + className);
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
