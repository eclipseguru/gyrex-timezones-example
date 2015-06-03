package tomezones.rest.api;

import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import tomezones.backend.services.User;

/**
 * Resource for users
 */
@Path("/users")
public class UsersResource extends ResourceRequiringAuthentication {

	private static final int DEFAULT_EXPIRY_TIME = 600;
	@Context
	private UriInfo uri;

	@POST
	@Path("/authenticate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(@FormParam("username") final String username, @FormParam("password") final String password) {
		final String token = userService.authenticate(username, password, DEFAULT_EXPIRY_TIME);
		if (token == null) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		final JSONObject o = new JSONObject();
		o.put("user", username);
		o.put("token", token);

		return Response.ok(o.toString(), MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(@FormParam("username") final String username, @FormParam("password") final String password) {
		final User user = userService.createUser(username, password);
		if (user == null) {
			return Response.status(Status.CONFLICT).build();
		}

		return Response.created(uri.getBaseUriBuilder().path(UsersResource.class).path("{id}").build(user.getName())).build();
	}

	@GET
	@Path("{username}")
	public Response getUser(@PathParam("username") final String username) {
		final String authenticatedUser = getAuthenticatedUser();
		if (!Objects.equals(username, authenticatedUser)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		final User user = userService.getUser(username);
		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return Response.ok(username, MediaType.TEXT_PLAIN).build();
	}
}
