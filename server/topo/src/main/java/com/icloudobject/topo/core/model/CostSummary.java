package com.icloudobject.topo.core.model;

public class CostSummary {
    String instanceType;
    double amount = 0;
    int hours = 0;
    String reserved;
    
    public CostSummary(String reserved, String instanceType) {
        this.reserved = reserved;
        this.instanceType = instanceType;
    }
    
    public String getInstanceType() {
        return instanceType;
    }
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public int getHours() {
        return hours;
    }
    public void setHours(int hours) {
        this.hours = hours;
    }
    public String getReserved() {
        return reserved;
    }
    public void setReserved(String reserved) {
        this.reserved = reserved;
    }
    public String toString() {
    	return "instanceType=" + instanceType + "," +
    			"amount=" + amount + "," +
    			"hours=" + hours + "," +
    			"reserved=" + reserved;
    }
    public String toJsonString() {
    	return "{\"instanceType\":\"" + instanceType + "\"," +
    			"\"amount\":" + amount + "," +
    			"\"hours\":" + hours + "," +
    			"\"reserved\":\"" + reserved + "\"}";
    }
    
}
