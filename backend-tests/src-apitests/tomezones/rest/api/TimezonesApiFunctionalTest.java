package tomezones.rest.api;

import static org.apache.http.client.fluent.Form.form;
import static org.apache.http.client.fluent.Request.Get;
import static org.apache.http.client.fluent.Request.Post;
import static org.apache.http.client.fluent.Request.Put;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.eclipse.gyrex.junit.GyrexServerResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.ClassRule;
import org.junit.Test;

public class TimezonesApiFunctionalTest {

	@ClassRule
	public static GyrexServerResource serverIsAvailable = new GyrexServerResource();

	final String baseUsersUri = "http://localhost:8080/api/users";
	final String baseTimezonesUri = "http://localhost:8080/api/timezones";

	private JSONArray assertCanGetTimezones(final String accessToken, final String nameFiler) throws Exception {
		// the get request
		final URIBuilder uriBuilder = new URIBuilder(baseTimezonesUri);
		if (nameFiler != null) {
			uriBuilder.addParameter("nameFilter", nameFiler);
		}
		final Response response = Get(uriBuilder.build()).addHeader("X-Access-Token", accessToken).execute();

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

			// parse body into JSON object
			final String bodyAsString = EntityUtils.toString(body);
			return new JSONArray(bodyAsString);
		} finally {
			response.discardContent();
		}
	}

	private void assertCanNotSaveTimezones(final JSONArray timezonesToSave, final String accessToken) throws Exception {
		// 400 is the code for BAD REQUEST
		final int expectedStatusCode = 400;

		assertSaveTimezonesReturnsStatusCode(timezonesToSave, accessToken, expectedStatusCode);
	}

	private void assertCanSaveTimezones(final JSONArray timezonesToSave, final String accessToken) throws Exception {
		// 200 is the code for OK
		final int expectedStatusCode = 200;

		assertSaveTimezonesReturnsStatusCode(timezonesToSave, accessToken, expectedStatusCode);
	}

	private void assertSaveTimezonesReturnsStatusCode(final JSONArray timezonesToSave, final String accessToken, final int expectedStatusCode) throws ClientProtocolException, IOException {
		// @formatter:off
		final Response response = Put(baseTimezonesUri)
				.addHeader("X-Access-Token", accessToken)
				.bodyString(timezonesToSave.toString(), ContentType.APPLICATION_JSON)
				.execute();
		// @formatter:on

		try {
			final HttpResponse httpResponse = response.returnResponse();
			assertNotNull(httpResponse);

			final StatusLine statusLine = httpResponse.getStatusLine();
			assertNotNull(statusLine);

			assertEquals(expectedStatusCode, statusLine.getStatusCode());
		} finally {
			response.discardContent();
		}
	}

	private void assertTimezonesEqual(final Object o1, final Object o2) {
		assertTrue(o1 instanceof JSONObject);
		assertTrue(o2 instanceof JSONObject);
		final JSONObject expected = (JSONObject) o1;
		final JSONObject actual = (JSONObject) o2;

		// the server might return additional data;
		// we only compare based on the definition of name, city and offset
		assertEquals(expected.get("name"), actual.get("name"));
		assertEquals(expected.get("city"), actual.get("city"));
		assertEquals(expected.getInt("offset"), actual.getInt("offset"));
	}

	private String authenticate(final String username, final String password) throws Exception {
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
			final String token = o.getString("token");
			assertNotNull("the response is expected to have a JSON field 'token'", token);
			return token;
		} finally {
			response.discardContent();
		}
	}

	private String createNewUserAndAuthenticate() throws Exception {
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();

		createUser(username, password);
		return authenticate(username, password);
	}

	private void createUser(final String username, final String password) throws Exception {
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
		} finally {
			response.discardContent();
		}
	}

	private JSONObject newTimezoneObject(final String name, final String city, final int offset) {
		final JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("city", city);
		o.put("offset", offset);
		return o;
	}

	@Test
	public void retrieve_timezones_for_new_user() throws Exception {
		final String token = createNewUserAndAuthenticate();

		final JSONArray timezones = assertCanGetTimezones(token, null);

		// for a new user, the array must be empty initially
		assertEquals(0, timezones.length());
	}

	@Test
	public void retrieve_timezones_with_filter() throws Exception {
		final String token = createNewUserAndAuthenticate();

		final JSONArray timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject("Germany", "Berlin", 1));
		timezonesToSave.put(newTimezoneObject("Office", "Vancouver", -8));
		timezonesToSave.put(newTimezoneObject("Home", "London", 0));

		assertCanSaveTimezones(timezonesToSave, token);

		// now retrieve it and ensure they match
		final JSONArray timezones = assertCanGetTimezones(token, "o");
		assertEquals(timezonesToSave.length() - 1, timezones.length());
		for (int i = 0; i < (timezonesToSave.length() - 1); i++) {
			assertTimezonesEqual(timezonesToSave.get(i + 1), timezones.get(i));
		}
	}

	@Test
	public void save_timezone_with_invalid_offset_fails() throws Exception {
		final String token = createNewUserAndAuthenticate();

		JSONArray timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject("Germany", "Berlin", 13));
		assertCanNotSaveTimezones(timezonesToSave, token);

		timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject("Germany", "Berlin", -13));
		assertCanNotSaveTimezones(timezonesToSave, token);

		timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject("Germany", "Berlin", Integer.MAX_VALUE));
		assertCanNotSaveTimezones(timezonesToSave, token);

		timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject("Germany", "Berlin", Integer.MIN_VALUE));
		assertCanNotSaveTimezones(timezonesToSave, token);
	}

	@Test
	public void save_timezone_without_city_fails() throws Exception {
		final String token = createNewUserAndAuthenticate();

		final JSONArray timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject("Germany", null, 1));
		assertCanNotSaveTimezones(timezonesToSave, token);
	}

	@Test
	public void save_timezone_without_name_fails() throws Exception {
		final String token = createNewUserAndAuthenticate();

		final JSONArray timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject(null, "Berlin", 1));

		assertCanNotSaveTimezones(timezonesToSave, token);
	}

	@Test
	public void save_timezones_and_retrieve_them() throws Exception {
		final String token = createNewUserAndAuthenticate();

		final JSONArray timezonesToSave = new JSONArray();
		timezonesToSave.put(newTimezoneObject("Germany", "Berlin", 1));
		timezonesToSave.put(newTimezoneObject("Office", "Vancouver", -8));

		assertCanSaveTimezones(timezonesToSave, token);

		// now retrieve it and ensure they match
		final JSONArray timezones = assertCanGetTimezones(token, null);
		assertEquals(timezonesToSave.length(), timezones.length());
		for (int i = 0; i < timezonesToSave.length(); i++) {
			assertTimezonesEqual(timezonesToSave.get(i), timezones.get(i));
		}
	}
}
