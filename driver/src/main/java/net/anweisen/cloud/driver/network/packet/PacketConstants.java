package net.anweisen.cloud.driver.network.packet;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PacketConstants {

	public static final int AUTH_CHANNEL = 1;                       // node    <-> master  <-> wrapper
	public static final int PUBLISH_CONFIG_CHANNEL = 2;             // master   -> node
	public static final int DATABASE_CHANNEL = 3;                   // node    <-> master  <-> wrapper
	public static final int REQUEST_API_CHANNEL = 4;                // master  <-> node
	public static final int SERVICE_INFO_PUBLISH_CHANNEL = 5;       // node    <-> master   -> wrapper
	public static final int SERVICE_UPDATE_SELF_INFO_CHANNEL = 6;   // wrapper  -> master
	public static final int PLAYER_API_CHANNEL = 7;                 // node    <-  master  <-> wrapper

	private PacketConstants() {}

}
