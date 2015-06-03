package tomezones.rest.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import tomezones.backend.services.Timezone;
import tomezones.backend.services.TimezoneService;

/**
 * /api - /users - GET .... list of users (ids) - POST .... creating user -
 * /<userid> - GET ... get user details for the user with id - DELETE -
 * /timezones - GET ... list of timezone (optional param for filter) - PUT ....
 * saving list timezone /<name> - GET ... get single timezone - DELTE ...
 * deleting timezone
 *
 */
@Path("/timezones")
public class TimezonesResource extends ResourceRequiringAuthentication {

	private static final TimezoneService timezoneService = new TimezoneService();

	@DELETE
	@Path("/{id}")
	public Response deleteTimezone(@PathParam("id") final String timeZoneId) {
		final String authenticatedUser = getAuthenticatedUser();

		// delete timezone in backen

		return Response.ok().build();
	}

	@GET
	@Path("/")
	public Response getTimezones(@QueryParam("nameFilter") final String nameFilter) {
		final String authenticatedUser = getAuthenticatedUser();

		final List<Timezone> timezones = timezoneService.getTimezones(authenticatedUser);

		final JSONArray array = new JSONArray();

		if (timezones != null) {
			for (final Timezone timezone : timezones) {
				if ((nameFilter != null) && (timezone.getName().toLowerCase().indexOf(nameFilter) == -1)) {
					continue;
				}
				array.put(toJsonObject(timezone));
			}
		}

		return Response.ok(array.toString(), MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveTimezones(final String jsonPayload) {
		final String authenticatedUser = getAuthenticatedUser();

		final List<Timezone> timezones = new ArrayList<>();

		final JSONArray array = new JSONArray(jsonPayload);
		for (int i = 0; i < array.length(); i++) {
			timezones.add(toTimezone(array.getJSONObject(i)));
		}

		timezoneService.setTimezones(authenticatedUser, timezones);

		return Response.ok().build();
	}

	private Object toJsonObject(final Timezone timezone) {
		if (timezone == null) {
			return JSONObject.NULL;
		}

		final JSONObject o = new JSONObject();
		o.put("name", timezone.getName());
		o.put("city", timezone.getCity());
		o.put("offset", timezone.getOffset());

		// Instant adjusted = (Instant)
		// ZoneOffset.ofHours(timezone.getOffset()).adjustInto(Instant.now(Clock.systemUTC()));
		// o.put("adjustedCurrentTime", adjusted.toString());
		return o;
	}

	private Timezone toTimezone(final JSONObject o) {
		if ((o == null) || (o == JSONObject.NULL)) {
			return null;
		}

		if (!o.has("name") || !o.has("city") || !o.has("offset")) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		final Timezone tz = new Timezone();
		try {
			tz.setName(o.getString("name"));
			tz.setCity(o.getString("city"));
			tz.setOffset(o.getInt("offset"));
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		return tz;
	}
}
