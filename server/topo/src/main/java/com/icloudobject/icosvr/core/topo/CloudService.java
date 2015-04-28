package com.icloudobject.icosvr.core.topo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Repository;

import com.ebay.cloud.cms.typsafe.entity.CMSQuery;
import com.ebay.cloud.cms.typsafe.entity.CMSQueryResult;
import com.ebay.cloud.cms.typsafe.entity.GenericCMSEntity;
import com.ebay.cloud.cms.typsafe.metadata.model.MetaRelationship;
import com.ebay.cloud.cms.typsafe.service.CMSClientContext;
import com.ebay.cloud.cms.typsafe.service.CMSClientService;
import com.icloudobject.icosvr.core.topo.model.ConfigNode;
import com.icloudobject.icosvr.model.ico.CloudConfig;

/*
 * This service provide API to kick off scan as well as 
 * return cloud topology
 */
@Repository
@SuppressWarnings("rawtypes")
public class CloudService extends AbstractService {

    /**
     * <pre>
     * Get a topo graph in depth level starting from one node
     * 
     * cloudConfig: the cloud definition node: The starting node level: maximum
     * 3 levels. When level is 0, return the node detail. When node is null,
     * return the default root to region refreshSeconds, if -1, get from DB,
     * otherwise, if the scan time is out of refresh seconds, kick off scan
     * again. before return data, scan the cloud first to get latest value
     * loadedNodes: nodes already traversed
     * </pre>
     */
    public ConfigNode getConfigGraph(CloudConfig cloudConfig, ConfigNode node, int level) throws IOException {
        Hashtable<String, ConfigNode> loadedNodes = new Hashtable<String, ConfigNode>();
        return getConfigGraph(cloudConfig, node, level, loadedNodes);
    }

    private ObjectNode wrapToJson(GenericCMSEntity en, ObjectMapper mapper) {
        ObjectNode ret = mapper.createObjectNode();
        ret.put("type", en.get_type());
        ret.put("id", en.get_id());
        ret.put("name", en.get_id());
        ArrayNode childNode = mapper.createArrayNode();
        for (String field : en.getFieldNames()) {
            Object obj = en.getFieldValue(field);
            if (obj == null)
                continue;
            if (obj instanceof Collection) {
                Collection subChildren = (Collection) obj;
                for (Object subChild : subChildren) {
                    if (subChild == null)
                        continue;
                    if (subChild instanceof GenericCMSEntity) {
                        childNode.add(wrapToJson((GenericCMSEntity) subChild, mapper));
                    }
                }
            } else if (obj instanceof GenericCMSEntity) {
                childNode.add(wrapToJson((GenericCMSEntity) obj, mapper));
            }
        }

        ret.put("children", childNode);
        return ret;
    }

    @SuppressWarnings("deprecation")
    public ArrayNode getNeighbors(CloudConfig cloudConfig, String type, String oid) {
        CMSClientService cms = super.getTopoRepoService(cloudConfig);
        ObjectMapper mapper = new ObjectMapper();
        GenericCMSEntity en = cms.get(oid, type);
        if (en == null) {
            return mapper.createArrayNode();
        }

        ArrayNode retArr = mapper.createArrayNode();
        ArrayList<GenericCMSEntity> childList = new ArrayList<GenericCMSEntity>();
        for (String field : en.getFieldNames()) {
            Object obj = en.getFieldValue(field);
            if (obj == null)
                continue;
            if (obj instanceof Collection) {
                Collection subChildren = (Collection) obj;
                for (Object subChild : subChildren) {
                    if (subChild == null)
                        continue;
                    if (subChild instanceof GenericCMSEntity) {
                        childList.add((GenericCMSEntity) subChild);
                    }
                }
            } else if (obj instanceof GenericCMSEntity) {
                childList.add((GenericCMSEntity) obj);

            }
            // }
            for (GenericCMSEntity subEn : childList) {
                ObjectNode node = mapper.createObjectNode();
                node.put("type", subEn.get_type());
                node.put("id", subEn.get_id());
                node.put("name", subEn.get_id());
                retArr.add(node);

            }

        }
        return retArr;
    }

    public ObjectNode loadQueryTree(CloudConfig cloudConfig, String queryString) {
        CMSClientService cms = super.getTopoRepoService(cloudConfig);
        CMSQuery query = new CMSQuery(queryString);
        query.setAllowFullTableScan(true);
        CMSQueryResult<GenericCMSEntity> result = cms.fullQuery(query, GenericCMSEntity.class, new CMSClientContext());
        ObjectMapper mapper = new ObjectMapper();

        if (result != null && result.getEntities() != null && result.getEntities().size() > 0) {
            GenericCMSEntity en = result.getEntities().get(0);
            return wrapToJson(en, mapper);
        }
        return mapper.createObjectNode();

    }

    /*
     * A recursive function to get the config nodes and its associated nodes.
     */
    ConfigNode getConfigGraph(CloudConfig cloudConfig, ConfigNode node, int level,
            Hashtable<String, ConfigNode> loadedNodes) throws IOException {
        if (node == null) {
            return null;
        } else if (loadedNodes.get(node.getId()) != null) {
            return loadedNodes.get(node.getId());
        }

        node = getConfigNode(cloudConfig, node);
        if (level > 0) {
            if (node.getRelationships() != null) {
                for (String key : node.getRelationships().keySet()) {
                    List<ConfigNode> cnList = node.getRelationships().get(key);
                    if (cnList != null && cnList.size() > 0) {
                        List<ConfigNode> newList = new ArrayList<ConfigNode>();
                        for (ConfigNode relNode : cnList) {
                            relNode = getConfigGraph(cloudConfig, relNode, level - 1, loadedNodes);
                            newList.add(relNode);
                        }
                        node.getRelationships().put(key, newList);
                    }
                }
            }
            if (node.getReverseRelationships() != null) {
                for (String key : node.getReverseRelationships().keySet()) {
                    List<ConfigNode> cnList = node.getReverseRelationships().get(key);
                    if (cnList != null && cnList.size() > 0) {
                        List<ConfigNode> newList = new ArrayList<ConfigNode>();
                        for (ConfigNode relNode : cnList) {
                            relNode = getConfigGraph(cloudConfig, relNode, level - 1, loadedNodes);
                            newList.add(relNode);
                        }
                        node.getReverseRelationships().put(key, newList);
                    }
                }
            }
            loadedNodes.put(node.getId(), node);
        }
        return node;
    }

    private String getIcoHomePath() {
        String base = System.getenv("ICO_TOPO_HOME");
        if (base != null && base.length() > 0) {
            return base;
        }
        return "/usr/local/ico-topo";

    }

    /*
     * refresh one node
     */
    public void refreshNode(CloudConfig cloudConfig, ConfigNode node) throws IOException, CloudException,
            InterruptedException {
        String className = node.getResourceType();
        String regionName = node.getRegionName();
        String oid = node.getId();
        String taskId = UUID.randomUUID().toString();
        String command = "python " + getIcoHomePath() + "/client/bin/ec2.py -a refresh -c " + cloudConfig.get_id()
                + " -t " + className + " -r " + oid + " -k " + taskId;
        if (regionName != null) {
            command = command + " -g " + regionName;
        }

        runCommand(command);
    }

    private void runCommand(String com) throws IOException, CloudException, InterruptedException {
        System.out.println("command:" + com);
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(com);
        String line = null;
        InputStream stderr = proc.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        try (BufferedReader br = new BufferedReader(isr)) {
            System.out.println("<PYTHON_STDERR>");
            while ((line = br.readLine()) != null)
                System.out.println(line);
            System.out.println("</PYTHON_STDERR>");
        }
        
        InputStream stdout = proc.getErrorStream();
        isr = new InputStreamReader(stdout);
        line = null;
        try (BufferedReader br = new BufferedReader(isr)) {
            System.out.println("<PYTHON_STDOUT>");
            while ((line = br.readLine()) != null)
                System.out.println(line);
            System.out.println("</PYTHON_STDOUT>");
        }
        int exitVal = proc.waitFor();
        System.out.println("Process exitValue: " + exitVal);
    }

    /*
     * kick off scan for one node
     */
    public void initScan(CloudConfig cloudConfig) throws IOException, CloudException, InterruptedException {
        String taskId = UUID.randomUUID().toString();
        String command = "python " + getIcoHomePath() + "/client/bin/ec2.py -a init -c " + cloudConfig.get_id()
                + " -k " + taskId;

        runCommand(command);
    }

    /*
     * kick off scan for one node
     */
    public void initBilling(CloudConfig cloudConfig) throws IOException, CloudException, InterruptedException {
        String command = "python " + getIcoHomePath() + "/client/bin/s3.py -a init -c " + cloudConfig.get_id();

        runCommand(command);
    }

    /*
     * get one node from CMS
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    private ConfigNode getConfigNode(CloudConfig cloudConfig, ConfigNode node) {
        String className = node.getResourceType();
        String oid = node.getId();
        CMSClientService cms = super.getTopoRepoService(cloudConfig);
        GenericCMSEntity entity = cms.get(oid, className);
        Collection<String> keys = entity.getFieldNames();
        Hashtable<String, Object> attrs = new Hashtable<String, Object>();
        Hashtable<String, List<ConfigNode>> relationships = new Hashtable<String, List<ConfigNode>>();
        for (String key : keys) {
            if (entity.getFieldValue(key) instanceof List) {
                if (entity.getFieldValue(key) != null && ((List) entity.getFieldValue(key)).size() > 0) {
                    if (((List) entity.getFieldValue(key)).get(0) instanceof GenericCMSEntity) {
                        List<ConfigNode> cnList = new ArrayList<ConfigNode>();
                        for (GenericCMSEntity oneEntity : (List<GenericCMSEntity>) entity.getFieldValue(key)) {
                            cnList.add(new ConfigNode(((GenericCMSEntity) oneEntity).get_type(),
                                    ((GenericCMSEntity) oneEntity).get_id()));
                        }
                        relationships.put(key, cnList);
                    } else {
                        attrs.put(key, entity.getFieldValue(key));
                    }
                }
            } else {
                if (entity.getFieldValue(key) instanceof GenericCMSEntity) {
                    List<ConfigNode> cnList = new ArrayList<ConfigNode>();
                    cnList.add(new ConfigNode(((GenericCMSEntity) entity.getFieldValue(key)).get_type(),
                            ((GenericCMSEntity) entity.getFieldValue(key)).get_id()));
                    relationships.put(key, cnList);
                } else {
                    attrs.put(key, entity.getFieldValue(key));
                }
            }
        }

        node.setAttrs(attrs);
        node.setRelationships(relationships);

        node.setReverseRelationships(getReverseRelathionships(cloudConfig, node));
        return node;
    }

    private Hashtable<String, List<ConfigNode>> getReverseRelathionships(CloudConfig cloudConfig, ConfigNode node) {
        CMSClientService cms = super.getTopoRepoService(cloudConfig);
        CMSClientContext context = new CMSClientContext();

        List<MetaRelationship> mrList = cms.getMetaRelationships(node.getResourceType(), context);

        Hashtable<String, List<ConfigNode>> result = new Hashtable<String, List<ConfigNode>>();
        String idQuery = node.getResourceType() + "[@_oid=\"" + node.getId() + "\"]";
        for (MetaRelationship mr : mrList) {
            if (mr.getRefDataType().equals(node.getResourceType())) {
                String q = mr.getName() + "!" + mr.getSrcDataType();
                CMSQuery query = new CMSQuery(idQuery + "." + mr.getName() + "!" + mr.getSrcDataType());
                query.setAllowFullTableScan(true);
                List<GenericCMSEntity> entities = cms.query(query, context).getEntities();
                if (entities != null && entities.size() > 0) {
                    List<ConfigNode> cnList = new ArrayList<ConfigNode>();
                    for (GenericCMSEntity entity : entities) {
                        ConfigNode child = new ConfigNode(mr.getSrcDataType(), entity.get_id());
                        cnList.add(child);
                    }
                    result.put(q, cnList);

                }
            }
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public boolean initDone(CloudConfig cloudConfig) {
        CMSClientService cms = super.getTopoRepoService(cloudConfig);
        GenericCMSEntity entity = cms.get("ROOT", "Cloud");
        if (entity == null) {
            return false;
        }

        String initStatus = (String) entity.getFieldValue("initStatus");
        if (initStatus != null && initStatus.equals("done")) {
            return true;
        } else {
            return false;
        }
    }

}
