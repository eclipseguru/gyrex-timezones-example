package tomezones.backend.services;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TimezoneService {

	private ConcurrentMap<String, List<Timezone>> timezonesByUser = new ConcurrentHashMap<>();

	public List<Timezone> getTimezones(String user) {
		return timezonesByUser.get(user);
	}

	public void setTimezones(String user, List<Timezone> timezones) {
		timezonesByUser.put(user, timezones);
	}

}
