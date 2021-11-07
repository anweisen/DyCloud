package net.anweisen.cloud.driver.player;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PlayerConstants {

	public static final String TABLE_NAME = "player";
	public static final String UUID_FIELD = "uniqueId";                     // UUID
	public static final String NAME_FIELD = "name";                         // String
	public static final String LANGUAGE_FIELD = "language";                 // String
	public static final String LAST_CONNECTION_FIELD = "lastConnection";    // PlayerProxyConnectionData
	public static final String FIRST_LOGIN_TIME_FIELD = "firstLogin";       // long
	public static final String LAST_ONLINE_TIME_FIELD = "lastOnline";       // long
	public static final String ONLINE_DURATION_FIELD = "onlineDuration";    // long
	public static final String PERMISSION_DATA_FIELD = "permissionData";    // PermissionData
	public static final String PROPERTIES_FIELD = "properties";             // Document

	private PlayerConstants() {}

}
