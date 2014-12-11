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
@Path("/repositories/{reponame}/branches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BranchResource extends com.ebay.cloud.cms.service.resources.impl.BranchResource {

}
