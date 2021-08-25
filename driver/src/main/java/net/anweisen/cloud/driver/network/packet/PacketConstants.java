package net.anweisen.cloud.driver.network.packet;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PacketConstants {

	public static final int RESPONSE_CHANNEL = -1;
	public static final int AUTH_CHANNEL = 1;                       // master  <-| node, wrapper, cord
	public static final int PUBLISH_CONFIG_CHANNEL = 2;             // master   -> node
	public static final int DATABASE_CHANNEL = 3;                   // master  <-| node, wrapper, cord
	public static final int REQUEST_API_CHANNEL = 4;                // master  <-> node
	public static final int SERVICE_INFO_PUBLISH_CHANNEL = 5;       // master   -> node, wrapper, cord
	public static final int SERVICE_UPDATE_SELF_INFO_CHANNEL = 6;   // wrapper  -> master
	public static final int PLAYER_API_CHANNEL = 7;                 // node    <-  master  <-> wrapper
	public static final int CORD_CHANNEL = 9;                       // cord     -> master   -> wrapper

	private static final Map<Integer, String> channelNames = new LinkedHashMap<>();
	static {
		for (Field field : PacketConstants.class.getFields()) {
			try {
				int channel = field.getInt(null);
				String name = field.getName().toLowerCase();
				if (name.endsWith("channel"))
					name = name.substring(0, name.indexOf("channel") - 1);

				channelNames.put(channel, name);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Nonnull
	public static String getChannelName(int channel) {
		return channelNames.getOrDefault(channel, channel + "");
	}

	private PacketConstants() {}

}
