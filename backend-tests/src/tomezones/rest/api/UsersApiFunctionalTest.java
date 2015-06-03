package tomezones.rest.api;

import static org.apache.http.client.fluent.Form.form;
import static org.apache.http.client.fluent.Request.Get;
import static org.apache.http.client.fluent.Request.Post;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.eclipse.gyrex.junit.GyrexServerResource;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class UsersApiFunctionalTest {

	@ClassRule
	public static GyrexServerResource serverIsAvailable = new GyrexServerResource();

	final String baseUsersUri = "http://localhost:8080/api/users";

	private String assertCanAuthenticate(final String username, final String password) throws Exception {
		// @formatter:off
		final Response response = Post(baseUsersUri + "/authenticate")
				.bodyForm(
						form()
						.add("username", username)
						.add("password", password)
						.build())
						.execute();
		// @formatter:on

		try {
			final HttpResponse httpResponse = response.returnResponse();
			assertNotNull(httpResponse);

			final StatusLine statusLine = httpResponse.getStatusLine();
			assertNotNull(statusLine);

			// 200 is the code for OK
			assertEquals(200, statusLine.getStatusCode());

			// the media type is expected to be JSON
			final HttpEntity body = httpResponse.getEntity();
			assertNotNull(body);
			assertNotNull(body.getContentType());
			assertEquals(MediaType.APPLICATION_JSON, body.getContentType().getValue());

			// parse response as JSON
			final String bodyAsString = EntityUtils.toString(body);
			final JSONObject o = new JSONObject(bodyAsString);
			assertNotNull("the response is expected to have a JSON field 'user'", o.getString("user"));
			assertEquals(username, o.getString("user"));
			final String token = o.getString("token");
			assertNotNull("the response is expected to have a JSON field 'token'", token);
			return token;
		} finally {
			response.discardContent();
		}
	}

	private void assertCanCreateUser(final String username, final String password) throws Exception {
		// @formatter:off
		final Response response = Post(baseUsersUri)
				.bodyForm(
						form()
						.add("username", username)
						.add("password", password)
						.build())
						.execute();
		// @formatter:on

		try {
			final HttpResponse httpResponse = response.returnResponse();
			assertNotNull(httpResponse);

			final StatusLine statusLine = httpResponse.getStatusLine();
			assertNotNull(statusLine);

			// 201 is the code for created
			assertEquals(201, statusLine.getStatusCode());

			// there should be a location header pointing to the created user
			final Header[] locations = httpResponse.getHeaders("Location");
			assertNotNull(locations);
			assertEquals(1, locations.length);
			assertNotNull(locations[0]);

			final String userUri = locations[0].getValue();
			assertNotNull(userUri);
			assertEquals(baseUsersUri + "/" + username, userUri);
		} finally {
			response.discardContent();
		}
	}

	private void assertCanGetUser(final String username, final String accessToken) throws Exception {
		// the get request
		final Response response = Get(baseUsersUri + "/" + username).addHeader("X-Access-Token", accessToken).execute();

		try {
			final HttpResponse httpResponse = response.returnResponse();
			assertNotNull(httpResponse);

			final StatusLine statusLine = httpResponse.getStatusLine();
			assertNotNull(statusLine);

			// 200 is the code for OK
			assertEquals(200, statusLine.getStatusCode());

			// the body must match the user name
			final HttpEntity body = httpResponse.getEntity();
			assertNotNull(body);
			assertEquals(username, EntityUtils.toString(body));
		} finally {
			response.discardContent();
		}
	}

	private void assertGetUserFailsWithForbidden(final String username, final String accessToken) throws Exception {
		// the get request
		final Response response = Get(baseUsersUri + "/" + username).addHeader("X-Access-Token", accessToken).execute();

		try {
			final HttpResponse httpResponse = response.returnResponse();
			assertNotNull(httpResponse);

			final StatusLine statusLine = httpResponse.getStatusLine();
			assertNotNull(statusLine);

			// 403 is the code for FORBIDDEN
			assertEquals(403, statusLine.getStatusCode());
		} finally {
			response.discardContent();
		}
	}

	private void assertGetUserRequiresAuthentication(final String username) throws Exception {
		// the get request
		final Response response = Get(baseUsersUri + "/" + username).execute();

		try {
			final HttpResponse httpResponse = response.returnResponse();
			assertNotNull(httpResponse);

			final StatusLine statusLine = httpResponse.getStatusLine();
			assertNotNull(statusLine);

			// 401 is the code for UNAUTHORIZED
			assertEquals(401, statusLine.getStatusCode());
		} finally {
			response.discardContent();
		}
	}

	@Test
	public void authenticate_and_retrieve_a_differnt_user_not_allowed() throws Exception {
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();

		// retrieving a user a user first, create one
		assertCanCreateUser(username, password);

		// then we need to sign in
		final String accessToken = assertCanAuthenticate(username, password);

		// accessing a different user must fail
		assertGetUserFailsWithForbidden("any-other-user", accessToken);

		// now the get call must succeed
		assertCanGetUser(username, accessToken);

		// now the get call must succeed
		try {
			assertCanGetUser("another-user", accessToken);
			fail("The GET user call for another user is expected to fail!");
		} catch (final AssertionError e) {
			// good
		}

	}

	@Test
	public void authenticate_and_retrieve_a_user() throws Exception {
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();

		// retrieving a user a user first, create one
		assertCanCreateUser(username, password);

		// then we need to sign in
		final String accessToken = assertCanAuthenticate(username, password);

		// now the get call must succeed
		assertCanGetUser(username, accessToken);
	}

	@Test
	public void create_a_user() throws Exception {
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();

		assertCanCreateUser(username, password);
	}

	@Test
	public void retrieving_any_user_without_authentication_fails() throws Exception {
		assertGetUserRequiresAuthentication("user" + System.nanoTime());
		assertGetUserRequiresAuthentication("user" + System.nanoTime());
		assertGetUserRequiresAuthentication("user" + System.nanoTime());
	}

	@Before
	public void setUp() throws Exception {
	}

}
