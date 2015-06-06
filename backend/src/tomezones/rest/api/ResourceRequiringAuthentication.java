package tomezones.rest.api;

import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import tomezones.backend.services.UserService;
import tomezones.backend.services.UserStore;

public class ResourceRequiringAuthentication {

	@Inject
	private UserStore userStore;

	@HeaderParam("X-Access-Token")
	private String accessTokenHeader;

	// in theory, a resource is single threaded; thus no fancy synchronization should be necessary
	private UserService userService;

	/**
	 * Checks if a valid token has been submitted in the authorization header
	 * and returns the email address from it.
	 *
	 * @return
	 */
	public String getAuthenticatedUser() {
		if (accessTokenHeader == null) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		final String email = getUserService().isAuthenticated(accessTokenHeader);
		if (email == null) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		return email;
	}

	protected UserService getUserService() {
		if (userService != null) {
			return userService;
		}
		return userService = new UserService(userStore);
	}
}
