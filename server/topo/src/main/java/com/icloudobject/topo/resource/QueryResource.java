/**
 * 
 */
package com.icloudobject.topo.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Liangfei
 *
 */
@Path("/repositories/{reponame}/branches/{branch}/query")
@Produces(MediaType.APPLICATION_JSON)
public class QueryResource extends com.ebay.cloud.cms.service.resources.impl.QueryResource {

}
