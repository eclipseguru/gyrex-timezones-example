package tomezones.backend.services;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTExpiredException;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTSigner.Options;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

public class UserService {

	private final String SECRET = "secret";
	final JWTSigner signer = new JWTSigner(SECRET);
	final JWTVerifier verifier = new JWTVerifier(SECRET);

	private final UserStore userStore;

	public UserService(UserStore userStore) {
		this.userStore = userStore;
	}

	/**
	 * Authenticates a user.
	 * <p>
	 * Returns a token that can be used as a trusted handle to identify a user.
	 * The token is time bound and will expire after a few minutes.
	 * </p>
	 *
	 * @param userName
	 * @param password
	 * @param expiryInSeconds
	 *            the token time-to-life in seconds
	 * @return a trusted token identifying a user (<code>null</code> if the user
	 *         is unknown or the password is wrong)
	 */
	public String authenticate(final String userName, final String password, final int expiryInSeconds) {
		final User user = userStore.get(userName);
		if (user == null) {
			return null;
		}
		if (!user.verifyPassword(password)) {
			return null;
		}

		final Options signingOptions = new Options().setExpirySeconds(expiryInSeconds);

		final HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("user", userName);
		return signer.sign(claims, signingOptions);
	}

	public User createUser(final String username, final String password) {
		final User user = new User(username, password);
		if (userStore.storeIfAbsent(user)) {
			return user;
		}

		return null;
	}

	public User getUser(final String user) {
		return userStore.get(user);
	}

	public String isAuthenticated(final String token) {
		try {
			final Map<String, Object> decodedPayload = verifier.verify(token);
			return (String) decodedPayload.get("user");
		} catch (final JWTExpiredException e) {
			return null;
		} catch (ClassCastException | InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException | IOException | JWTVerifyException e) {
			e.printStackTrace();
			return null;
		}

	}
}
