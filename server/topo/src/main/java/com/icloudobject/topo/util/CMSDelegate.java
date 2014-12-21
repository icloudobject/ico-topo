/**
 * 
 */
package com.icloudobject.topo.util;

import com.ebay.cloud.cms.service.resources.impl.BranchResource;
import com.ebay.cloud.cms.service.resources.impl.CMSStateResource;
import com.ebay.cloud.cms.service.resources.impl.ConfigResource;
import com.ebay.cloud.cms.service.resources.impl.EntityResource;
import com.ebay.cloud.cms.service.resources.impl.MetadataResource;
import com.ebay.cloud.cms.service.resources.impl.MonitorResource;
import com.ebay.cloud.cms.service.resources.impl.QueryResource;
import com.ebay.cloud.cms.service.resources.impl.RootResource;
import com.ebay.cloud.cms.service.resources.impl.ServiceResource;

/**
 * @author Liangfei
 *
 */
public class CMSDelegate {

    private static CMSDelegate delegate;

    private CMSDelegate() {
    }

    public static CMSDelegate getInstance() {
        if (delegate == null) {
            synchronized (CMSDelegate.class) {
                if (delegate == null) {
                    delegate = new CMSDelegate();
                }
            }
        }
        return delegate;
    }

    private final MetadataResource metaResource = new MetadataResource();

    private final CMSStateResource stateResource = new CMSStateResource();

    private final BranchResource branchResource = new BranchResource();

    private final ConfigResource configResource = new ConfigResource();

    private final EntityResource entityResource = new EntityResource();

    private final MonitorResource monitorResource = new MonitorResource();

    private final QueryResource queryResource = new QueryResource();

    private final RootResource rootResource = new RootResource();

    private final ServiceResource serviceResource = new ServiceResource();

    public MetadataResource getMetaResource() {
        return metaResource;
    }

    public CMSStateResource getStateResource() {
        return stateResource;
    }

    public BranchResource getBranchResource() {
        return branchResource;
    }

    public ConfigResource getConfigResource() {
        return configResource;
    }

    public EntityResource getEntityResource() {
        return entityResource;
    }

    public MonitorResource getMonitorResource() {
        return monitorResource;
    }

    public QueryResource getQueryResource() {
        return queryResource;
    }

    public RootResource getRootResource() {
        return rootResource;
    }

    public ServiceResource getServiceResource() {
        return serviceResource;
    }

}
