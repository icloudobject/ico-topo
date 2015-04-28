package com.icloudobject.icosvr.web.topo.vo;

import java.util.HashSet;
import java.util.Map;

public class D3TreeNode {
	public String id,type,name,color;
	public HashSet<D3TreeNode> children=new HashSet<D3TreeNode>();
	public static D3TreeNode createNodeFrom(GraphNode node,Map<String,String> typeColorMap) {
		HashSet<String> discoveredNodes=new HashSet<String>();
		if(node==null) return null;
		return discover(discoveredNodes,null,node,0,50,typeColorMap);
	} 

	public D3TreeNode(String id,String type,String name,String color) {
		this.id=id;
		this.type=type;
		this.name=name;
		this.color=color;
		if(this.color==null) this.color="black";
	}
	private static D3TreeNode discover(HashSet<String> discoveredNodes,D3TreeNode parent,GraphNode node,int curDepth,int maxDepth,Map<String,String> typeColorMap) {
		if(node==null || node.getId()==null) return null;
		if(curDepth>maxDepth) return null;
		String treeNodeId=null;
		//if(parent==null) {
			treeNodeId=node.getId();
		//} else {
		//	treeNodeId=parent.id+"-"+node.getId();
		//}
		D3TreeNode n=new D3TreeNode(treeNodeId,node.getType(),node.getName(),typeColorMap.get(node.getType()));
		if(parent!=null) {
			parent.children.add(n);
		}
		//if already discovered, then ignore the drilldown
		if(discoveredNodes.add(node.getId())) {
			for(GraphNode next:node.getNeighbors()) {
				discover(discoveredNodes,n,next,curDepth+1,maxDepth,typeColorMap);
			}
		}
		return n;
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
		D3TreeNode other = (D3TreeNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}





}
