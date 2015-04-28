package com.icloudobject.icosvr.core.topo.model;

public class Instance {
    public enum State {
        RUNNING, STOPPED, TERMINATED
    }
    
    private String instanceId;
    
    private String name;
    
    private String instanceType;
    
    private String region;
    
    private String zone;
    private String os;
    
    private State state;
    
    public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getInstanceType() {
        return instanceType;
    }
    
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getZone() {
        return zone;
    }
    
    public void setZone(String zone) {
        this.zone = zone;
    } 
    
}
