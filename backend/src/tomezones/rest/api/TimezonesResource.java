package tomezones.rest.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import tomezones.backend.services.Timezone;
import tomezones.backend.services.TimezoneService;

/**
 * /api - /users - GET .... list of users (ids) - POST .... creating user - /
 * <userid> - GET ... get user details for the user with id - DELETE -
 * /timezones - GET ... list of timezone (optional param for filter) - PUT ....
 * saving list timezone /<name> - GET ... get single timezone - DELTE ...
 * deleting timezone
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
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimezoneDto> getTimezones(@QueryParam("nameFilter") final String nameFilter) {
		final String authenticatedUser = getAuthenticatedUser();

		final List<Timezone> timezones = timezoneService.getTimezones(authenticatedUser);

		final List<TimezoneDto> array = new ArrayList<TimezoneDto>();

		if (timezones != null) {
			for (final Timezone timezone : timezones) {
				if ((nameFilter != null) && (timezone.getName().toLowerCase().indexOf(nameFilter) == -1)) {
					continue;
				}
				array.add(toJsonObject(timezone));
			}
		}

		return array;
	}

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveTimezones(final TimezoneDto[] timezoneDtos) {
		final String authenticatedUser = getAuthenticatedUser();

		final List<Timezone> timezones = new ArrayList<>();

		for (int i = 0; i < timezoneDtos.length; i++) {
			timezones.add(toTimezone(timezoneDtos[i]));
		}

		timezoneService.setTimezones(authenticatedUser, timezones);

		return Response.ok().build();
	}

	private TimezoneDto toJsonObject(final Timezone timezone) {
		if (timezone == null) {
			return null;
		}

		final TimezoneDto o = new TimezoneDto();
		o.name = timezone.getName();
		o.city = timezone.getCity();
		o.offset = timezone.getOffset();

		// Instant adjusted = (Instant)
		// ZoneOffset.ofHours(timezone.getOffset()).adjustInto(Instant.now(Clock.systemUTC()));
		// o.put("adjustedCurrentTime", adjusted.toString());
		return o;
	}

	private Timezone toTimezone(final TimezoneDto o) {
		if (o == null) {
			return null;
		}

		if ((o.name == null) || (o.city == null) || (o.offset == null)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		final Timezone tz = new Timezone();
		try {
			tz.setName(o.name);
			tz.setCity(o.city);
			tz.setOffset(o.offset);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		return tz;
	}
}
