package tomezones.backend.services;

/**
 * A store for {@link User} information
 */
public interface UserStore {

	User get(String userName);

	/**
	 * @return <code>true</code> if successfully stored, <code>false</code> if
	 *         it already existed and the user option
	 */
	boolean storeIfAbsent(User user);

}
