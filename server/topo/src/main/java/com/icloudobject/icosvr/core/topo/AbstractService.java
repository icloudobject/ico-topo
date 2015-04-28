package com.icloudobject.icosvr.core.topo;

import java.util.List;

import com.ebay.cloud.cms.typsafe.entity.CMSQuery;
import com.ebay.cloud.cms.typsafe.service.CMSClientContext;
import com.ebay.cloud.cms.typsafe.service.CMSClientService;
import com.icloudobject.icosvr.intg.cms.AbstractCmsDao;
import com.icloudobject.icosvr.model.ico.CloudConfig;

public class AbstractService extends AbstractCmsDao {
    public String getResourceId(String cloudName, String resourceId) {
        return cloudName + ":" + resourceId;
    }

    public String getCloudResourceId(String resourceId) {
        return resourceId.split(":")[1];
    }

    public List<com.icloudobject.icosvr.model.cloud.aws.Instance> getCMSQueryResult(String query, CloudConfig cloudConfig) {
        CMSClientContext context = new CMSClientContext();
        CMSClientService cms = super.getTopoRepoService(cloudConfig);
        CMSQuery cmsQuery = new CMSQuery(query);
        cmsQuery.setAllowFullTableScan(true);
        return cms.fullQuery(cmsQuery, com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
    }

}
