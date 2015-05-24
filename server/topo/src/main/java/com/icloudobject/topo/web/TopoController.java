package com.icloudobject.topo.web;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.icloudobject.icosvr.model.ico.CloudConfig;
import com.icloudobject.topo.core.model.ConfigNode;
import com.icloudobject.topo.core.service.CloudService;
import com.icloudobject.topo.skeleton.AwsCloudConfig;

/**
 * Handle topo actions
 * 
 * @author bw
 * 
 */
@Controller
public class TopoController {

	private static final String CLOUD_INSTANCE_QUERY = "Cloud{@id}.cloud!Region{@id}.region!AvailabilityZone{@id}.availabilityZone!Instance{@id}";

    private static Logger logger = LoggerFactory.getLogger(TopoController.class);

	@Autowired
    private CloudService cloudsService;
	
	@RequestMapping(value = "/api/topo/", method = RequestMethod.POST)
	@ResponseBody
	public String initTopo(/*final @AuthenticationPrincipal IcoUser currentUser*/) throws Exception {
		CloudConfig cloudConfig = AwsCloudConfig.getInstance();//  currentUser.getCloudConfig();
		cloudsService.initScan(cloudConfig);
		
        if (cloudsService.initDone(cloudConfig)) {
            ObjectNode node = cloudsService.loadQueryTree(cloudConfig, CLOUD_INSTANCE_QUERY);
            return node.toString();
        } else {
			Exception e = new Exception("Cloud first scan not finished yet.");
			logger.error("", e);
			throw e;
		}
	}

    @RequestMapping(value = "/api/topo/", method = RequestMethod.GET)
    @ResponseBody
    public String checkTopo() {
        CloudConfig cloudConfig = AwsCloudConfig.getInstance();
        if (cloudsService.initDone(cloudConfig)) {
            ObjectNode node = cloudsService.loadQueryTree(cloudConfig, CLOUD_INSTANCE_QUERY);
            return node.toString();
        } else {
            return "[]";
        }
    }

    @RequestMapping(value = "/api/topo/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        return "Hello, Topo";
    }

	@RequestMapping(value = "/api/topo_next/{resource}/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getNeighbor(/*final @AuthenticationPrincipal IcoUser currentUser,*/
			@PathVariable("resource") String resourceType,
			@PathVariable("id") String resourceId) throws Exception {
		CloudConfig cloudConfig = AwsCloudConfig.getInstance();// currentUser.getCloudConfig();
		
		if (cloudsService.initDone(cloudConfig)) {
			ArrayNode arr=cloudsService.getNeighbors(cloudConfig, resourceType,resourceId);
			return arr.toString();
		} else {
			throw new Exception("Cloud first scan not finished yet.");
		}
	}


	@RequestMapping(value = "/api/topo/{resource}/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getTopo(/*final @AuthenticationPrincipal IcoUser currentUser,*/
			@PathParam("resource") String resourceType,
			@PathParam("id") String resourceId,
			@QueryParam("level") int level,
			final HttpServletResponse response) throws Exception {
//		GenericJsonResponseBuilder builder=new GenericJsonResponseBuilder();
//		if (currentUser == null) {
//			builder.throwUnauthException();
//		}
		if (level >3) {
			level = 3;
		}
		CloudConfig cloudConfig = AwsCloudConfig.getInstance(); // currentUser.getCloudConfig();
		if (cloudsService.initDone(cloudConfig)) {
			ConfigNode cn = new ConfigNode(resourceType, resourceId);
			cn = cloudsService.getConfigGraph(cloudConfig, cn, level);
			//return cn.toJsonString(null);
			// 20150213 - fixed with a temp change - To be confirmed by Ferry
			return cn.toJsonString();

		} else {
			throw new Exception("Cloud first scan not finished yet.");
		}
	}

}
