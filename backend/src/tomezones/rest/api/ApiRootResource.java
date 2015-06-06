package tomezones.rest.api;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root of the Timezone API
 */
@Path("/")
public class ApiRootResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "Hello! " + new Date();
	}
}
