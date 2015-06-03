package tomezones.backend.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class TimezoneServiceTest {

	private TimezoneService service;

	private void assertSetAndGetTimezones(String user, ArrayList<Timezone> timezones) {
		assertNull(service.getTimezones(user));
		service.setTimezones(user, timezones);
		assertNotNull(service.getTimezones(user));
		assertSame(timezones, service.getTimezones(user));
	}

	@Test
	public void get_and_set_timezones_per_user() throws Exception {
		assertSetAndGetTimezones("user" + System.nanoTime(), new ArrayList<>());
		assertSetAndGetTimezones("user" + System.nanoTime(), new ArrayList<>());
	}

	@Before
	public void setup() {
		service = new TimezoneService();
	}

}
