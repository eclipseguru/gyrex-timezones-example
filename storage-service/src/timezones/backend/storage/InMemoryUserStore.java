package timezones.backend.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import tomezones.backend.services.User;
import tomezones.backend.services.UserStore;

public class InMemoryUserStore implements UserStore {

	private final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();

	@Override
	public User get(String userName) {
		return users.get(userName);
	}

	@Override
	public boolean storeIfAbsent(User user) {
		return users.putIfAbsent(user.getName(), user) == null;
	}

}
