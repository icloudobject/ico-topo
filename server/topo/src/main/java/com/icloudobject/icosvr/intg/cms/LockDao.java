package com.icloudobject.icosvr.intg.cms;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ebay.cloud.cms.typsafe.entity.CMSQuery;
import com.ebay.cloud.cms.typsafe.service.CMSClientContext;
import com.ebay.cloud.cms.typsafe.service.CMSClientService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.icloudobject.icosvr.model.ico.Lock;

@Repository
public class LockDao extends AbstractCmsDao {

    public String create(final Lock lock) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(lock.getTenantId()), "tenant id could not be empty!");
        CMSClientContext context = new CMSClientContext();
        CMSClientService cms = super.getSysRepoService();
        Lock l = null;
        try {
            l = cms.create(lock, context);
        } catch (Exception e) {
            return "NotAllow";
            // e.printStackTrace();
        }
        if (l != null) {
            return "OK";
        } else {
            return "FAIL";
        }
    }

    public String delete(final Lock lock) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(lock.getTenantId()), "tenant id could not be empty!");
        CMSClientContext context = new CMSClientContext();
        CMSClientService cms = super.getSysRepoService();
        CMSQuery query = new CMSQuery("Lock[@tenantId=\"" + lock.getTenantId() + "\" and @lock=\"" + lock.getLock() + "\"]");
        query.setAllowFullTableScan(true);
        List<Lock> locks = cms.fullQuery(query, Lock.class, context).getEntities();
        if (locks != null && locks.size() > 0) {
            Lock l = locks.get(0);
            if (l.getRequestor().equals(lock.getRequestor())) {
                cms.delete(l, context);
                return "OK";
            } else {
                return "NotAllow";
            }
        } else {
            return "NA";
        }
    }
    
}
