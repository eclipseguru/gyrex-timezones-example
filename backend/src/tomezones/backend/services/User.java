package tomezones.backend.services;

import java.util.Objects;

public class User {

	private String name, password;

	public User() {
		// empty
	}

	public User(String name, String password) {
		setName(name);
		setPassword(password);
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean verifyPassword(String password) {
		// in real word, password would be hashed and this needs hash comparison
		// (eg., bcrypt)
		return Objects.equals(this.password, password);
	}

}
