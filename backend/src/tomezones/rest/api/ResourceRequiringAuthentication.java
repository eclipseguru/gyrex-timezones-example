package tomezones.rest.api;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import tomezones.backend.services.UserService;

public class ResourceRequiringAuthentication {

	protected static UserService userService = new UserService();

	@HeaderParam("X-Access-Token")
	private String accessTokenHeader;

	/**
	 * Checks if a valid token has been submitted in the authorization header
	 * and returns the email address from it.
	 *
	 * @return
	 */
	public String getAuthenticatedUser() {
		if (accessTokenHeader == null)
			throw new WebApplicationException(Status.UNAUTHORIZED);
		String email = userService.isAuthenticated(accessTokenHeader);
		if (email == null)
			throw new WebApplicationException(Status.UNAUTHORIZED);
		return email;
	}
}
