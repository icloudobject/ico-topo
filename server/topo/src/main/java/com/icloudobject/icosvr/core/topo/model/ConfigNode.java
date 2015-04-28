package com.icloudobject.icosvr.core.topo.model;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/*
 * This is the generic ConfigNode class
 * When attrs and relationships equals to null, it is a reference only class 
 */
public class ConfigNode {
    // the oid in CMS
    String id;
    
    // the resource type eg. Instance
    String resourceType;
    
    // the resourceId
    String resourceId;
    
    // the region for this resource
    String regionName;
    
    // the attr list
    Hashtable<String,Object> attrs;
    
    // the relationships
    Hashtable<String,List<ConfigNode>> reverseRelationships;


    // the relationships
    Hashtable<String,List<ConfigNode>> relationships;


    public ConfigNode(String resourceType, String resourceId) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.id = resourceId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Hashtable<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Hashtable<String, Object> attrs) {
        this.attrs = attrs;
    }

    
    private String toJsonFieldStr(String key, String value) {
        return "\"" + key + "\":\"" + value + "\"";
    }
    
    
    
    public Hashtable<String, List<ConfigNode>> getReverseRelationships() {
        return reverseRelationships;
    }

    public void setReverseRelationships(
            Hashtable<String, List<ConfigNode>> reverseRelationships) {
        this.reverseRelationships = reverseRelationships;
    }

    public Hashtable<String, List<ConfigNode>> getRelationships() {
        return relationships;
    }

    public void setRelationships(Hashtable<String, List<ConfigNode>> relationships) {
        this.relationships = relationships;
    }

    @SuppressWarnings("rawtypes")
    public String toJsonString() {
        Hashtable traversed = new Hashtable();
        return toJsonString(null, traversed);
    }
     
    @SuppressWarnings({ "rawtypes", "unchecked" })
    String toJsonString(String parentId, Hashtable traversed) {
       
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append(toJsonFieldStr("id", id));
        sb.append(",");
        sb.append(toJsonFieldStr("type", resourceType));
        sb.append(",");
        sb.append(toJsonFieldStr("resourceId", resourceId));
        sb.append(",");
        if (regionName != null) {
            sb.append(toJsonFieldStr("region", regionName));
            sb.append(",");
        }
        if (attrs != null) {
            Set<String> keys = attrs.keySet();
            for (String key: keys) {
                if (!key.startsWith("_")) {
                    sb.append(toJsonFieldStr(key, attrs.get(key).toString()));
                    sb.append(",");
                }
            }
        }
        
        if (traversed.get(id) == null) {
            traversed.put(id, 1);
            if (relationships != null) {
                Set<String> keys = relationships.keySet();
                for (String key: keys) {
                    List<ConfigNode> cnList = relationships.get(key);
                    if (cnList!= null && cnList.size() > 0) {
                        sb.append("\"" + key + "\":" + "[");
                        for (ConfigNode cn : cnList) {
                            if (parentId == null || !cn.getId().equals(parentId)) {
                                sb.append(cn.toJsonString(id, traversed) + ",");
                            }
                        }
                        sb.setLength(sb.length() - 1);
    
                        sb.append("]");
                    }
                    sb.append(",");
                }
            }
            
            if (reverseRelationships != null) {
                Set<String> keys = reverseRelationships.keySet();
                for (String key: keys) {
                    List<ConfigNode> cnList = reverseRelationships.get(key);
                    if (cnList!= null && cnList.size() > 0) {
                        sb.append("\"" + key + "\":" +"[");
                        for (ConfigNode cn : cnList) {
                            if (parentId == null || !cn.getId().equals(parentId)) {
                                sb.append(cn.toJsonString(parentId, traversed) + ",");
                            }
                        }
                        sb.setLength(sb.length() - 1);
                        sb.append("]");
                    }
                    sb.append(",");
                }
            }
        }
        
        sb.setLength(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }
    
}
