/**
 * 
 */
package com.icloudobject.topo.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Liangfei
 *
 */
@Path("/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource extends com.ebay.cloud.cms.service.resources.impl.ConfigResource {}
