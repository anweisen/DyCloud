package net.anweisen.cloud.driver.network.packet;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PacketConstants {

	public static final int RESPONSE_CHANNEL = -1;
	public static final int AUTH_CHANNEL = 1;                       // master  <-| node, wrapper
	public static final int DATABASE_CHANNEL = 2;                   // master  <-| node, wrapper
	public static final int REQUEST_API_CHANNEL = 3;                // master  <-| node, wrapper
	public static final int NODE_DATA_CYCLE = 4;                    // master  <-  node
	public static final int NODE_INFO_PUBLISH_CHANNEL = 5;          // master   -> node, wrapper
	public static final int SERVICE_INFO_PUBLISH_CHANNEL = 6;       // node     -> master                -> node, wrapper
	public static final int SERVICE_UPDATE_SELF_INFO_CHANNEL = 7;   // wrapper  -> master
	public static final int SERVICE_CONTROL_CHANNEL = 8;            // node    <-  master                <- wrapper, node
	public static final int PLAYER_EVENT_CHANNEL = 9;               // wrapper  -> master
	public static final int PLAYER_EXECUTOR_CHANNEL = 10;           // wrapper <-  master                <- wrapper, node
	public static final int PLAYER_REMOTE_MANAGER_CHANNEL = 11;     // master  <-| wrapper, node
	public static final int MODULE_SYSTEM_CHANNEL = 12;             // master  <-| node
	public static final int GLOBAL_CONFIG_CHANNEL = 13;             // master  <-> wrapper, node

	private static final Map<Integer, String> channelNames = new LinkedHashMap<>();
	static {
		for (Field field : PacketConstants.class.getFields()) {
			try {
				registerChannelName(field.getName(), field.getInt(null));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void registerChannelName(@Nonnull String name, int channel) {
		name = name.toLowerCase();
		if (name.endsWith("channel"))
			name = name.substring(0, name.indexOf("channel") - 1);

		if (channelNames.containsKey(channel))
			System.err.println("PacketConstants: " + name + " and " + channelNames.get(channel) + " share the same value: " + channel);

		channelNames.put(channel, name);
	}

	@Nonnull
	public static String getChannelName(int channel) {
		return channelNames.getOrDefault(channel, channel + "");
	}

	private PacketConstants() {}

}
