/**
 * 
 */
package com.icloudobject.topo.skeleton;

import com.icloudobject.icosvr.model.ico.CloudConfig;
import com.icloudobject.topo.core.Cloud;

/**
 * A simple aws cloud config provider to cooperate with the the multi-tenant
 * design.
 * 
 * @author Liangfei
 *
 */
public class AwsCloudConfig extends CloudConfig {
    
    private static final AwsCloudConfig INSTANCE = new AwsCloudConfig();

    public static AwsCloudConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public String getTopoRepoName() {
        return "AwsCloudTopo";
    }

    @Override
    public String getCloudName() {
        return Cloud.AWS.getCloudName();
    }
}
