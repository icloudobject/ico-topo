

package com.icloudobject.icosvr.core.topo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ebay.cloud.cms.typsafe.entity.CMSQuery;
import com.ebay.cloud.cms.typsafe.entity.CMSQuery.SortOrder;
import com.ebay.cloud.cms.typsafe.service.CMSClientContext;
import com.ebay.cloud.cms.typsafe.service.CMSClientService;
import com.icloudobject.icosvr.core.topo.model.CostSummary;
import com.icloudobject.icosvr.model.ico.CloudConfig;

@Repository
public class CostService extends AbstractService {
    
    private com.icloudobject.icosvr.core.topo.model.InstanceCost convertAwsInstanceCost(com.icloudobject.icosvr.model.cloud.aws.InstanceCost awsInstanceCost) {
        com.icloudobject.icosvr.core.topo.model.InstanceCost instCost = new com.icloudobject.icosvr.core.topo.model.InstanceCost();
        // manufacture the Instance object based on Topo data
        instCost.setInstanceId(getResourceId("AWS", awsInstanceCost.getInstanceId()));
        instCost.setRate(awsInstanceCost.getRate());
        instCost.setCost(awsInstanceCost.getCost());
        instCost.setReserved(awsInstanceCost.getReserved().equals("Y"));
        instCost.setStartHour(awsInstanceCost.getUsageStartDate());
        return instCost;
    }
    
    public List<com.icloudobject.icosvr.core.topo.model.InstanceCost> getInstanceCosts(
            final CloudConfig cloudConfig) {
        if (cloudConfig.getCloudName().equals("AWS")) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("InstanceCost");
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.InstanceCost> awsInstanceCosts = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.InstanceCost.class, context).getEntities();
            List<com.icloudobject.icosvr.core.topo.model.InstanceCost> instanceCosts = new ArrayList<>();
            for (int i = 0; i < awsInstanceCosts.size(); i++) {
                instanceCosts.add(convertAwsInstanceCost(awsInstanceCosts.get(i)));
            }
            return instanceCosts;
        } else {
            return null;
        }
    }
    
    public com.icloudobject.icosvr.core.topo.model.InstanceCost getLatestInstanceCost(
            final CloudConfig cloudConfig, String instanceId) {
        if (cloudConfig.getCloudName().equals("AWS")) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("InstanceCost[@instanceId=\"" + instanceId + "\"]");
            query.addSortOn("usageStartDate");
            query.addSortOrder(SortOrder.desc);
            query.setLimits(new long[] { 1 });
            
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.InstanceCost> awsInstanceCosts = cms.query(query,
                    com.icloudobject.icosvr.model.cloud.aws.InstanceCost.class, context).getEntities();
            
            if (awsInstanceCosts != null && awsInstanceCosts.size() > 0) {
                return convertAwsInstanceCost(awsInstanceCosts.get(0));
            }
        }
        return null;
    }
    
    public double getBilledRatio(CloudConfig cloudConfig, String instanceId, Date startTime, Date endTime) throws Exception {
        
        if (cloudConfig.getCloudName().equals("AWS")) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("InstanceCost[@instanceId=\"" + instanceId + "\"" 
                    + " and @usageStartDate <= Date(" + endTime.getTime() + ") " 
                    + " and @usageStartDate >= Date(" + startTime.getTime() + ")]");
            query.addSortOn("usageStartDate");
            query.addSortOrder(SortOrder.desc);
            
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.InstanceCost> awsInstanceCosts = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.InstanceCost.class, context).getEntities();
            if (awsInstanceCosts == null || awsInstanceCosts.size() == 0) {
                return 0;
            }
            int count = awsInstanceCosts.size();
            endTime = ((com.icloudobject.icosvr.model.cloud.aws.InstanceCost) awsInstanceCosts.get(0)).getUsageStartDate();
            startTime = ((com.icloudobject.icosvr.model.cloud.aws.InstanceCost) awsInstanceCosts.get(count-1)).getUsageStartDate();
            long duration = endTime.getTime() - startTime.getTime();
            return (duration/1000/60/60/count);
            
        }
        return 0; // (known billed time/total billed period)
    
    }
    
    public List<CostSummary> getCostBreakdown(CloudConfig cloudConfig, Date startTime, Date endTime) {
    	if (startTime == null) {
    		Calendar calendar = Calendar.getInstance();
    		calendar.add( Calendar.MONTH ,  -1 );
    		startTime = calendar.getTime();
    	}
        if (cloudConfig.getCloudName().equals("AWS")) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            String queryStr = "InstanceCost[";
            if (endTime != null) {
            	queryStr = queryStr + " @usageStartDate <= date(" + endTime.getTime() + ") and ";
            }
            queryStr = queryStr + " @usageStartDate >= date(" + startTime.getTime() + ")]";
            CMSQuery query = new CMSQuery(queryStr);
            List<com.icloudobject.icosvr.model.cloud.aws.InstanceCost> awsInstanceCosts = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.InstanceCost.class, context).getEntities();
            if (awsInstanceCosts == null || awsInstanceCosts.size() == 0) {
                return null;
            }
            Hashtable<String,CostSummary>  costHash = new Hashtable<String,CostSummary>();
            for (int i= 0; i < awsInstanceCosts.size(); i++) {
                com.icloudobject.icosvr.model.cloud.aws.InstanceCost ic = awsInstanceCosts.get(i);
                String key = ic.getReserved() + ":" + ic.getInstanceType();
                CostSummary cs = costHash.get(key);
                if (cs == null) {
                    cs = new CostSummary(ic.getReserved(),ic.getInstanceType());
                }
                cs.setHours(cs.getHours() + 1);
                cs.setAmount(cs.getAmount() + Double.parseDouble(ic.getCost()));
                costHash.put(key, cs);
            }
            return new ArrayList<>(costHash.values());
        }
        return null;
        
    }
}
