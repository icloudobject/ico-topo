package com.icloudobject.icosvr.web.topo.vo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Not thread safe,id is unique id.
 * 
 * @author fenwang
 * 
 */
public class GraphNode {

	@Autowired
//	private static ProcessDao InstanceManager;

	private String id, type, name;
	private Set<GraphNode> neighbors = new HashSet<GraphNode>();

	public GraphNode(String id, String type, String name) {
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public Set<GraphNode> getNeighbors() {
		return this.neighbors;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	// public static GraphNode getTenantTree(String tenantId) {
	// GraphNode root = new GraphNode("0", "Tenent", tenantId);
	// List<Region> regions = InstanceManager.getRegions(tenantId);
	// for (int regionIndex = 0; regionIndex < regions.size(); regionIndex++) {
	// Region r = regions.get(regionIndex);
	// GraphNode rn = new GraphNode(r.get_id(), "Region", r.getName());
	// root.getNeighbors().add(rn);
	// List<Zone> azs = InstanceManager.findAZByRegion(tenantId, r.getName());
	// for (int azIndex = 0; azIndex < azs.size(); azIndex++) {
	// Zone az = azs.get(azIndex);
	// GraphNode azn = new GraphNode(az.get_id(), "AvailabilityZone", az.getName());
	// rn.getNeighbors().add(azn);
	// List<Instance> hs = InstanceManager.getInstancesByAZ(tenantId, az.getName());
	// for (int InstanceIndex = 0; InstanceIndex < hs.size(); InstanceIndex++) {
	// Instance Instance = hs.get(InstanceIndex);
	// GraphNode hn = new GraphNode(Instance.get_id(), "Instance", Instance.getInstanceId());
	// azn.getNeighbors().add(hn);
	// List<Process> processes = Instance.getProcesses();
	// for (int processIndex = 0; processIndex < processes.size(); processIndex++) {
	// Process pro = processes.get(processIndex);
	// GraphNode pn = new GraphNode(pro.get_id(), "Process", pro.getCommand());
	// hn.getNeighbors().add(pn);
	// }
	// }
	// }
	// }
	//
	// return root;
	// }

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		/*
		 * // sample output GraphNode tenant = new GraphNode("1", "Tenant", "demo"); // add cloud GraphNode aws = new
		 * GraphNode("2", "Cloud", "AWS"); tenant.getNeighbors().add(aws); // add AZ GraphNode dc1 = new GraphNode("3",
		 * "AZ", "dc_slc"); aws.getNeighbors().add(dc1); // add instance GraphNode compute1 = new GraphNode("4",
		 * "Compute", "demo01.dev.aws.com"); dc1.getNeighbors().add(compute1); GraphNode compute2 = new GraphNode("5",
		 * "Compute", "demo02.dev.aws.com"); dc1.getNeighbors().add(compute2); // add process GraphNode process1 = new
		 * GraphNode("6", "Process", "Tomcat run"); compute1.getNeighbors().add(process1); GraphNode process2 = new
		 * GraphNode("7", "Process", "mongod"); compute1.getNeighbors().add(process2);
		 * compute2.getNeighbors().add(process1); compute2.getNeighbors().add(process2);
		 */
		// GraphNode tenant = GraphNode.getTenantTree("demo");
		// ObjectMapper mapper = new ObjectMapper();
		// String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tenant);
		// System.out.println(jsonString);
	}

}
