package tomezones.backend.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UserServiceTest {

	private UserService service;

	private void assertCanCreateUser(final String username, final String password) {
		assertNull(service.getUser(username));

		final User createdUser = service.createUser(username, password);
		assertNotNull(createdUser);
		assertEquals(username, createdUser.getName());
		assertTrue(createdUser.verifyPassword(password));
	}

	private void assertCanRetrieveUserAndPasswordVerifies(final String username, final String password) {
		final User retrievedUsers = service.getUser(username);
		assertNotNull(retrievedUsers);
		assertEquals(username, retrievedUsers.getName());
		assertTrue(retrievedUsers.verifyPassword(password));
	}

	private void assertIsAuthenticated(final String username, final String token) {
		final String authenticatedUsername = service.isAuthenticated(token);
		assertNotNull(authenticatedUsername);
		assertEquals(username, authenticatedUsername);
	}

	private void assertIsNotAuthenticated(final String token) {
		assertNull("token must not authenticated", service.isAuthenticated(token));
	}

	@Test
	public void authentication_token_expires() throws Exception {
		// given
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();
		assertCanCreateUser(username, password);
		assertCanRetrieveUserAndPasswordVerifies(username, password);

		// when
		final String token = service.authenticate(username, password, 1);

		// then
		assertNotNull(token);
		assertIsAuthenticated(username, token);

		// when
		Thread.sleep(1000);

		// then
		assertIsNotAuthenticated(token);
	}

	@Test
	public void can_authenticate_and_verify() throws Exception {
		// given
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();
		assertCanCreateUser(username, password);
		assertCanRetrieveUserAndPasswordVerifies(username, password);

		// when
		final String token = service.authenticate(username, password, 30);

		// then
		assertNotNull(token);
		assertIsAuthenticated(username, token);
	}

	@Test
	public void cannot_authenticate_with_wrong_password() throws Exception {
		// given
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();
		assertCanCreateUser(username, password);
		assertCanRetrieveUserAndPasswordVerifies(username, password);

		// when
		final String token = service.authenticate(username, "wrong password", 30);

		// then
		assertNull(token);
	}

	@Test
	public void cannot_authenticate_with_wrong_user() throws Exception {
		// given
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();
		assertCanCreateUser(username, password);
		assertCanRetrieveUserAndPasswordVerifies(username, password);

		// when
		final String token = service.authenticate("wrong user", password, 30);

		// then
		assertNull(token);
	}

	@Test
	public void create_and_get_user() throws Exception {
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();

		assertCanCreateUser(username, password);
		assertCanRetrieveUserAndPasswordVerifies(username, password);

		// subsequent gets should still work
		assertCanRetrieveUserAndPasswordVerifies(username, password);
		assertCanRetrieveUserAndPasswordVerifies(username, password);
	}

	@Test
	public void create_duplicate_user_fails() throws Exception {
		final String username = "user" + System.nanoTime();
		final String password = "password" + System.nanoTime();

		assertCanCreateUser(username, password);
		assertCanRetrieveUserAndPasswordVerifies(username, password);

		// second create call must fail
		assertNull(service.createUser(username, password));
		assertNull(service.createUser(username, "any other password"));

		// should still be able to retrieve using original password
		assertCanRetrieveUserAndPasswordVerifies(username, password);
	}

	@Before
	public void setUp() throws Exception {
		service = new UserService();
	}

}
