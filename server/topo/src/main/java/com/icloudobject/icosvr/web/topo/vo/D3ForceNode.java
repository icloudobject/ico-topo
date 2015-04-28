package com.icloudobject.icosvr.web.topo.vo;

import java.util.HashSet;
import java.util.Map;

public class D3ForceNode {
	
	public static class Node {
		public  Node(String id,String type, String name,String color, String content) {
			this.id=id;
			this.type=type;
			this.name=name;
			this.color=color;
			this.content=content;
		}
		public String id,type,name,color, content;
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
			Node other = (Node) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
		
	}
	public static class Link {
		public Link(String source, String target) {
			this.source=source;
			this.target=target;
		}
		
		@Override
		public int hashCode() {
			return 31*source.hashCode()*target.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Link other = (Link) obj;
			if(source.equals(other.source) && target.equals(other.target)) return true;
			if(source.equals(other.target) && target.equals(other.source)) return true;
			return false;
		}

		public String source,target,value;
	}
	
	public HashSet<Node> nodes=new HashSet<Node>();
	public HashSet<Link> links=new HashSet<Link>();
	public D3ForceNode(GraphNode node,Map<String,String> typeColorMap) {
		discover(node,typeColorMap);
	}
	private void discover(GraphNode node,Map<String,String> typeColorMap) {
		if(node==null || node.getId()==null) return;
		Node n=new Node(node.getId(),node.getType(),node.getName(),typeColorMap.get(node.getType()),"");
		if(!nodes.add(n)) return;
		if(node.getNeighbors()==null) return;
		for(GraphNode other:node.getNeighbors()) {
			links.add(new Link(node.getId(),other.getId()));
			discover(other,typeColorMap);
		}
	}

}
