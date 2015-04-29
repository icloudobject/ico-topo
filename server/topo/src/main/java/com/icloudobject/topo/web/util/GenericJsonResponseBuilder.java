package com.icloudobject.topo.web.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class GenericJsonResponseBuilder {
	private boolean success=false;
	private LinkedHashMap<String,Object> map=null;
	private ArrayList<Object> arr=null;
	public GenericJsonResponseBuilder success() {
		success=true;
		return this;
	}
	public GenericJsonResponseBuilder map() {
		this.map=new LinkedHashMap<String,Object>();
		return this;
	}
	public GenericJsonResponseBuilder array() {
		this.arr=new ArrayList<Object>();
		return this;
	}
	
	public GenericJsonResponseBuilder add(String key,Object val) {
		if(this.map!=null) {
			this.map.put(key, val);
		}
		return this;
	}
	public GenericJsonResponseBuilder add(Object val) {
		if(this.arr!=null) {
			this.arr.add(val);
		}
		return this;
	}
	
	public GenericJsonResponse build() {
		if(this.map!=null) {
			return new GenericJsonResponse(success,this.map);
		} else if(this.arr!=null) {
			return new GenericJsonResponse(success,this.arr);
		} else {
			return new GenericJsonResponse(success,null);
		}	
	}
	
	public void throwUnauthException() {
		throw new UnAuthorizedException();
	}
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public class UnAuthorizedException extends RuntimeException {
		private static final long serialVersionUID = 6015003618232342057L;
	    
	}
}
