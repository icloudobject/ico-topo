/**
 * www.iCloudObject.com
 * 
 */
package com.icloudobject.topo.intg.cms;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ebay.cloud.cms.typsafe.metadata.model.MetaClass;
import com.ebay.cloud.cms.typsafe.metadata.model.MetadataManager;
import com.ebay.cloud.cms.typsafe.metadata.model.Repository;
import com.ebay.cloud.cms.typsafe.metadata.model.RepositoryOption;
import com.google.common.base.Preconditions;
import com.icloudobject.icosvr.model.ico.Lock;
import com.icloudobject.icosvr.model.ico.MetricSet;
import com.icloudobject.icosvr.model.ico.Tenant;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;

@org.springframework.stereotype.Repository
@SuppressWarnings("deprecation")
public class MetadataLoader {

	private static final Logger logger = LoggerFactory.getLogger(MetadataLoader.class);

	@Value("${cms.url}")
	private String cmsUrl;

	@Value("${cms.sys.repo}")
	private String cmsSysRepo;

	private static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public MetadataLoader() {
	}

	/**
	 * Check and init the sys repo. Use raw connection.
	 */
//	@PostConstruct
	private void sysInit() {
		Client client = null;
		try {
			client = Client.create();
			client.addFilter(new GZIPContentEncodingFilter(false));
			client.setReadTimeout(60 * 1000);
			client.setConnectTimeout(300 * 1000);

			// config the cms service
			WebResource resource = client.resource(this.cmsUrl + "/config");
			String configStr = "{\"MetadataDelete\": true, \"SysAllowRepositoryDelete\":true, "
					+ "\"SysDalMigrationDualWrite\": false, \"SysDalDefaultImplementation\": 1, "
					+ "\"DefaultSysLimitMaxIndexesNum\": 30}";
			resource.accept(MediaType.APPLICATION_JSON_TYPE).entity(configStr, MediaType.APPLICATION_JSON_TYPE).post();

			// check ico_sys repo
			resource = client.resource(this.cmsUrl + "/repositories/" + this.cmsSysRepo);
			ClientResponse rsp = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
			if (rsp.getStatus() == Status.NOT_FOUND.getStatusCode()) {
				try {
					// not found, create sys repo
					resource = client.resource(this.cmsUrl + "/repositories/");
					Repository repo = new Repository();
					repo.setRepositoryName(this.cmsSysRepo);
					repo.setOptions(new RepositoryOption());
					String value = mapper.writeValueAsString(repo);
					resource.accept(MediaType.APPLICATION_JSON_TYPE).entity(value, MediaType.APPLICATION_JSON_TYPE)
							.post();

					// now load sys metadatas
					resource = client.resource(this.cmsUrl + "/repositories/" + this.cmsSysRepo + "/metadata");
					value = mapper.writeValueAsString(getSysMetadatas());
					resource.accept(MediaType.APPLICATION_JSON_TYPE).entity(value, MediaType.APPLICATION_JSON_TYPE)
							.post();
				} catch (Exception e) {
					throw new SystemInitializationException(
							"system initilaization faile due to ico_sys db create/load failure!", e);
				}

				// load InstanceCapacty and InstacncePrice data
				URL instanceUrl = MetadataLoader.class.getClassLoader().getResource("init-data/instances.json");
				ArrayNode instances = (ArrayNode) mapper.readTree(instanceUrl);
				for (int index = 0; index < instances.size(); index++) {
					JsonNode instance = instances.get(index);
					StringBuffer capacity = new StringBuffer();
					capacity.append("{");
					capacity.append("\"cloudType\":\"aws\",");
					capacity.append("\"instanceType\":");
					capacity.append(instance.get("instance_type"));
					capacity.append(",");
					capacity.append("\"cpuUnit\":");
					capacity.append(instance.get("vCPU"));
					capacity.append(",");
					capacity.append("\"memorySize\":");
					capacity.append(instance.get("memory"));
					capacity.append("");
					capacity.append("}");

					resource = client.resource(this.cmsUrl + "/repositories/ico/branches/main/InstanceCapacity");
					resource.accept(MediaType.APPLICATION_JSON_TYPE)
							.entity(capacity.toString(), MediaType.APPLICATION_JSON_TYPE).post();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				client.destroy();
			}
		}
	}

	// actually load once and only once. By spring config?
	public List<MetaClass> getTenantMetadatas() {
		try {
			ArrayNode nodes = readJarJson("com.icloudobject.icosvr.model.tenant");
			List<MetaClass> results = new ArrayList<>();
			List<MetaClass> metas = new ArrayList<MetaClass>(MetadataManager.load(nodes, false).getMetadatas().values());
			for (MetaClass meta : metas) {
				if (meta.getName().equals(Tenant.class.getSimpleName())
						|| meta.getName().equals(Lock.class.getSimpleName())
						|| meta.getName().equals(MetricSet.class.getSimpleName())) {
					continue;
				}
				results.add(meta);
			}
			return results;
		} catch (Exception e) {
			logger.error("load meta failed!", e);
			throw new RuntimeException("load tenant metadata failed!", e);
		}
	}
	
	   // actually load once and only once. By spring config?
    public List<MetaClass> getTopoMetadatas(String cloudName) {
        try {
            ArrayNode nodes = readJarJson("com.icloudobject.icosvr.model.cloud." + cloudName);
            List<MetaClass> results = new ArrayList<>();
            List<MetaClass> metas = new ArrayList<MetaClass>(MetadataManager.load(nodes, false).getMetadatas().values());
            for (MetaClass meta : metas) {
                if (meta.getName().equals(Tenant.class.getSimpleName())
                        || meta.getName().equals(Lock.class.getSimpleName())
                        || meta.getName().equals(MetricSet.class.getSimpleName())) {
                    continue;
                }
                results.add(meta);
            }
            return results;
        } catch (Exception e) {
            logger.error("load meta failed!", e);
            throw new RuntimeException("load tenant metadata failed!", e);
        }
    }

	public List<MetaClass> getSysMetadatas() {
		try {
			ArrayNode nodes = readJarJson("com.icloudobject.icosvr.model.ico");
			List<MetaClass> results = new ArrayList<MetaClass>(MetadataManager.load(nodes, false).getMetadatas()
					.values());
			return results;
		} catch (Exception e) {
			logger.error("load meta failed!", e);
			throw new RuntimeException("load tenant metadata failed!", e);
		}
	}

	private ArrayNode readJarJson(String packagePath) throws Exception {
		packagePath = packagePath.replace('.', '/');

		ArrayNode resultNode = JsonNodeFactory.instance.arrayNode();

		Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packagePath);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			// ZIP file doesn't support file:/ prefix
			String[] segments = url.getFile().split("!");
			String jarPath = segments[0];
			if (jarPath.startsWith("file:")) {
				jarPath = jarPath.substring("file:".length());
			}

			File file = new File(jarPath);
			if (!file.exists() || !jarPath.endsWith(".jar")) {
				logger.info(MessageFormat.format("Ignore reading json from extracted {0} from full path {1}", jarPath,
						url.getFile()));
				continue;
			}
			logger.debug("Reading from jar:" + jarPath);

			// read all the json files inside the jar file; filter out the
			// file based on prefix and suffix
			ZipFile zf = new ZipFile(file);
			try {
				Enumeration<? extends ZipEntry> zips = zf.entries();
				while (zips.hasMoreElements()) {
					String zipEntryName = zips.nextElement().getName();
					if (zipEntryName.contains(packagePath) && zipEntryName.endsWith(".json")) {
						logger.info("Reading inside jar json:" + zipEntryName);
						URL jsonUrl = Thread.currentThread().getContextClassLoader().getResource(zipEntryName);
						JsonNode fileNode = mapper.readTree(jsonUrl);
						addJsonNode(resultNode, fileNode);
					}
				}
			} finally {
				try {
					zf.close();
				} catch (Exception e) {
					logger.error("unable to close the zip file:" + zf.getName(), e);
				}
			}
		}

		Preconditions.checkState(resultNode.size() > 0, "No meta loaded for generation!");
		return resultNode;
	}

	private void addJsonNode(ArrayNode resultNode, JsonNode fileNode) {
		if (fileNode.isArray()) {
			ArrayNode an = (ArrayNode) fileNode;
			resultNode.addAll(an);
		} else {
			resultNode.add(fileNode);
		}
	}
}
