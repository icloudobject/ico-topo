/**
 * www.iCloudObject.com
 * 
 */
package com.icloudobject.icosvr.web.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author liasu
 * 
 */
@Component
public class ModelSummaryHelper {

    private Map<String, List<String>> summaries = new HashMap<>();

    public ModelSummaryHelper() {
        // FIXME:configurable??
        summaries.put("AvailabilityZone", Arrays.asList("region"));
        summaries.put("DnsName", Arrays.asList("dnsName"));
        summaries.put("DnsRecord", Arrays.asList("dnsRecordId"));

        summaries.put("Host", Arrays.asList("instanceId", "cloudType", "instanceType", "hostType"));
        summaries.put("Image", Arrays.asList("imageId"));
        summaries.put("Ip", Arrays.asList("ip"));

        summaries.put("NetworkInterface", Arrays.asList("networkInterfaceId"));
        summaries.put("Process", Arrays.asList("processId", "command"));
        summaries.put("Region", Arrays.asList("regionName"));
        summaries.put("SecurityGroup", Arrays.asList("secGroupId"));
        summaries.put("Subnet", Arrays.asList("subnetId"));
    }

    public List<String> getSummaryFields(String modelType) {
        if (summaries.containsKey(modelType)) {
            return summaries.get(modelType);
        } else {
            return Collections.emptyList();
        }
    }
}
