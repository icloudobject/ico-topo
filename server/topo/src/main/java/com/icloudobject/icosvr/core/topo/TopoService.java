package com.icloudobject.icosvr.core.topo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ebay.cloud.cms.typsafe.entity.CMSQuery;
import com.ebay.cloud.cms.typsafe.service.CMSClientContext;
import com.ebay.cloud.cms.typsafe.service.CMSClientService;
import com.icloudobject.icosvr.core.topo.model.Instance;
import com.icloudobject.icosvr.model.ico.CloudConfig;

@Repository
public class TopoService extends AbstractService {
	public final String CLOUD_AWS = "aws";
    
    private com.icloudobject.icosvr.core.topo.model.Instance convertAwsInstance(
    		CloudConfig cloudConfig, com.icloudobject.icosvr.model.cloud.aws.Instance awsInstance) {
        com.icloudobject.icosvr.core.topo.model.Instance inst = new com.icloudobject.icosvr.core.topo.model.Instance();
        // manufacture the Instance object based on Topo data
        inst.setInstanceId(getResourceId(CLOUD_AWS, awsInstance.getId()));
        inst.setInstanceType(awsInstance.getInstanceType());
        String zoneId = awsInstance.getAvailabilityZone().get_id();
        inst.setZone(zoneId);
        inst.setState(Instance.State.valueOf(awsInstance.getState().toUpperCase()));
        int len = zoneId.length();
        inst.setRegion(zoneId.substring(0,len-1));
        String platform = getImagePlatform(cloudConfig, awsInstance);
        if (platform != null && platform.equals("windows")) {
        	inst.setOs("windows");
        } else {
            inst.setOs("linux");
        }
        return inst;
    }

    private String getImagePlatform(CloudConfig cloudConfig,
            com.icloudobject.icosvr.model.cloud.aws.Instance awsInstance) {
        if (awsInstance.getImage() == null) {
            return null;
        }

        String imageId = awsInstance.getImage().get_id();
        CMSClientContext context = new CMSClientContext();
        CMSClientService cms = super.getTopoRepoService(cloudConfig);
        CMSQuery query = new CMSQuery("Image[@id=\"" + imageId + "\"]");
        query.setAllowFullTableScan(true);
        List<com.icloudobject.icosvr.model.cloud.aws.Image> images = cms.fullQuery(query,
                com.icloudobject.icosvr.model.cloud.aws.Image.class, context).getEntities();
        if (images.size() > 0) {
            return images.get(0).getPlatform();
        }
        return null;
    }

    public com.icloudobject.icosvr.core.topo.model.Instance getInstance (
            final CloudConfig cloudConfig, final String instanceId) {
        if (cloudConfig.getCloudName().equals(CLOUD_AWS)) {
            
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("Instance[@id=\"" + getCloudResourceId(instanceId)
                    + "\"]");
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.Instance> awsInstances = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
            if (awsInstances != null && awsInstances.size() > 0) {
                return convertAwsInstance(cloudConfig, awsInstances.get(0));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // bug
    public List<com.icloudobject.icosvr.core.topo.model.Instance> getInstances(final CloudConfig cloudConfig) {
        if (cloudConfig.getCloudName().equals(CLOUD_AWS)) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("Instance");
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.Instance> awsInstances = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
            List<com.icloudobject.icosvr.core.topo.model.Instance> instances = new ArrayList<>();
            for (int i = 0; i < awsInstances.size(); i++) {
                instances.add(convertAwsInstance(cloudConfig, awsInstances.get(i)));
            }
            return instances;
        } else {
            return null;
        }
    }

    public com.icloudobject.icosvr.core.topo.model.Instance getInstanceByInstanceId(final CloudConfig cloudConfig,
            final String instanceId) {
        return getOneByQuery(cloudConfig, "Instance[@instanceId=\"" + instanceId + "\"]");
    }

    private com.icloudobject.icosvr.core.topo.model.Instance getOneByQuery(final CloudConfig cloudConfig,
            final String queryString) {
        if (cloudConfig.getCloudName().equals(CLOUD_AWS)) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTenantRepoService(cloudConfig);
            CMSQuery query = new CMSQuery(queryString);
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.Instance> awsInstances = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
            if (awsInstances != null && awsInstances.size() > 0) {
                return convertAwsInstance(cloudConfig, awsInstances.get(0));
            }
        }
        return null;
    }

    public com.icloudobject.icosvr.core.topo.model.Instance getInstanceByMac(final CloudConfig cloudConfig,
            final String macAddress) {
        return getOneByQuery(cloudConfig, "Instance[@macAddress=\"" + macAddress + "\"]");
    }

    public List<com.icloudobject.icosvr.core.topo.model.Instance> getInstancesByRegion(
            final CloudConfig cloudConfig, String regionId) {
        if (cloudConfig.getCloudName().equals(CLOUD_AWS)) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("Region[@id=\"" + regionId + "\"].region!AvailabilityZone.availabilityZone!Instance[@state!=\"terminated\"]");
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.Instance> awsInstances = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
            List<com.icloudobject.icosvr.core.topo.model.Instance> instances = new ArrayList<>();
            for (int i = 0; i < awsInstances.size(); i++) {
                instances.add(convertAwsInstance(cloudConfig, awsInstances.get(i)));
            }
            return instances;
        } else {
            return null;
        }
    }
    
    public List<com.icloudobject.icosvr.core.topo.model.Instance> getInstancesByZone(
            final CloudConfig cloudConfig, String zoneId) {
        if (cloudConfig.getCloudName().equals(CLOUD_AWS)) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("Instance[@state!=\"terminated\"]{*}.availabilityZone[@id=\"" + zoneId + "\"]");
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.Instance> awsInstances = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
            List<com.icloudobject.icosvr.core.topo.model.Instance> instances = new ArrayList<>();
            for (int i = 0; i < awsInstances.size(); i++) {
                instances.add(convertAwsInstance(cloudConfig, awsInstances.get(i)));
            }
            return instances;
        } else {
            return null;
        }
    }
    
    public List<com.icloudobject.icosvr.core.topo.model.Instance> getRunningInstances(
            final CloudConfig cloudConfig) {
        if (cloudConfig.getCloudName().equals(CLOUD_AWS)) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("Instance[@state=\"running\"]");
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.Instance> awsInstances = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
            List<com.icloudobject.icosvr.core.topo.model.Instance> instances = new ArrayList<>();
            for (int i = 0; i < awsInstances.size(); i++) {
                instances.add(convertAwsInstance(cloudConfig, awsInstances.get(i)));
            }
            return instances;
        } else {
            return null;
        }
    }
    
    public List<com.icloudobject.icosvr.core.topo.model.Instance> getNotRunningInstances(
            final CloudConfig cloudConfig) {
        if (cloudConfig.getCloudName().equals(CLOUD_AWS)) {
            CMSClientContext context = new CMSClientContext();
            CMSClientService cms = super.getTopoRepoService(cloudConfig);
            CMSQuery query = new CMSQuery("Instance[@state!=\"running\"]");
            query.setAllowFullTableScan(true);
            List<com.icloudobject.icosvr.model.cloud.aws.Instance> awsInstances = cms.fullQuery(query,
                    com.icloudobject.icosvr.model.cloud.aws.Instance.class, context).getEntities();
            List<com.icloudobject.icosvr.core.topo.model.Instance> instances = new ArrayList<>();
            for (int i = 0; i < awsInstances.size(); i++) {
                instances.add(convertAwsInstance(cloudConfig, awsInstances.get(i)));
            }
            return instances;
        } else {
            return null;
        }
    }
}
