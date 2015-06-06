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
	public AuthenticateUserResponse authenticate(@FormParam("username") final String username, @FormParam("password") final String password) {
		final String token = getUserService().authenticate(username, password, DEFAULT_EXPIRY_TIME);
		if (token == null) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		final AuthenticateUserResponse response = new AuthenticateUserResponse();
		response.user = username;
		response.token = token;

		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(@FormParam("username") final String username, @FormParam("password") final String password) {
		final User user = getUserService().createUser(username, password);
		if (user == null) {
			return Response.status(Status.CONFLICT).build();
		}

		return Response.created(uri.getBaseUriBuilder().path(UsersResource.class).path("{id}").build(user.getName())).build();
	}

	@GET
	@Path("/_status")
	@Produces(MediaType.TEXT_PLAIN)
	public String getStatus() {
		try {
			getUserService().getUser("dummy");
			return "OK";
		} catch (final IllegalStateException e) {
			throw new WebApplicationException(Response.status(501).entity("DOWN").type(MediaType.TEXT_PLAIN_TYPE).build());
		}
	}

	@GET
	@Path("{username}")
	public Response getUser(@PathParam("username") final String username) {
		final String authenticatedUser = getAuthenticatedUser();
		if (!Objects.equals(username, authenticatedUser)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		final User user = getUserService().getUser(username);
		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return Response.ok(username, MediaType.TEXT_PLAIN).build();
	}
}
