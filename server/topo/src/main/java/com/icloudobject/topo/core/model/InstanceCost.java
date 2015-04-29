package com.icloudobject.topo.core.model;

import java.util.Date;

public class InstanceCost {
    private boolean isReserved;
    private String rate;
    private String cost;
    private String instanceId;
    private Date startHour;
    
    public boolean isReserved() {
        return isReserved;
    }
    public void setReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }
    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public Date getStartHour() {
        return startHour;
    }
    public void setStartHour(Date startHour) {
        this.startHour = startHour;
    }
}
