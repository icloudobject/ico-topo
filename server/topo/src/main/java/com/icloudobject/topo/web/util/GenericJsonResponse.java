package com.icloudobject.topo.web.util;

public class GenericJsonResponse {
	public boolean success=false;
	public Object data=null;
	public GenericJsonResponse(boolean success,Object data) {
		this.success=success;
		this.data=data;
	} 
	
}
