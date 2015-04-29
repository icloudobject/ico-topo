package com.icloudobject.topo.intg.cms;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ebay.cloud.cms.typsafe.service.CMSClientService;
import com.ebay.cloud.cms.typsafe.service.ext.CMSService;
import com.icloudobject.icosvr.model.ico.CloudConfig;
import com.icloudobject.topo.core.Cloud;

/**
 * Parent class of all DAOs, providing common methods to obtain CMS client, etc.
 * 
 * @author cyrus
 * 
 */
@Repository
public abstract class AbstractCmsDao {

    @Value("${cms.url}")
    private String cmsUrl;

    @Value("${cms.sys.repo}")
    private String sysRepo;

    @Value("${cms.sys.repo.code.prefix}")
    private String sysRepoCodePrefix;

    @Value("${cms.tenant.repo.code.prefix}")
    private String tenantRepoCodePrefix;
    
    @Value("${cms.cloudprovider.repo.code.prefix}")
    private String cloudProviderRepoCodePrefix;

    private CMSService cmsService;

    protected AbstractCmsDao() {
    }
    
    @PostConstruct
    private void init()
    {
        this.cmsService = new CMSService(this.cmsUrl);
    }

    /**
     * @return the ico sys repo client
     */
    protected CMSClientService getSysRepoService() {
        return this.cmsService.getClientService(this.sysRepo, this.sysRepoCodePrefix);
    }

    /**
     * @return the tenant repo client
     */
    protected CMSClientService getTenantRepoService(final CloudConfig cloudConfig) {
        return this.cmsService.getClientService(cloudConfig.getTenantRepoName(), this.tenantRepoCodePrefix);
    }

    /**
     * @return the vendor specific repo client
     */
    protected CMSClientService getTopoRepoService(final CloudConfig cloudConfig) {
        Cloud c = Cloud.fromString(cloudConfig.getCloudName());
        String packageName = cloudProviderRepoCodePrefix + "." + c.getCloudName();
        return this.cmsService.getClientService(cloudConfig.getTopoRepoName(), packageName);
    }

}
