package net.anweisen.cloud.modules.bridge.helper;

import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.wrapper.CloudWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BridgeHelper {

	private static int maxPlayers, lastOnlineCount;
	private static String motd, phase, extra;

	/**
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#MAX_PLAYERS
	 */
	public static int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#MAX_PLAYERS
	 */
	public static void setMaxPlayers(int maxPlayers) {
		BridgeHelper.maxPlayers = maxPlayers;
	}

	/**
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#ONLINE_PLAYERS
	 */
	public static int getLastOnlineCount() {
		return lastOnlineCount;
	}

	/**
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#ONLINE_PLAYERS
	 */
	public static void setOnlineCount(int lastOnlineCount) {
		BridgeHelper.lastOnlineCount = lastOnlineCount;
	}

	/**
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#EXTRA
	 */
	public static String getMotd() {
		return motd;
	}

	/**
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#EXTRA
	 */
	public static void setMotd(@Nonnull String motd) {
		BridgeHelper.motd = motd;
	}

	/**
	 * A string representing the current phase of the service like LOBBY, FULL, INGAME, defaults to LOBBY when started
	 *
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#PHASE
	 */
	public static String getPhase() {
		return phase;
	}

	/**
	 * A string representing the current phase of the service like LOBBY, FULL, INGAME, defaults to LOBBY when started
	 *
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#PHASE
	 */
	public static void setPhase(@Nullable String phase) {
		BridgeHelper.phase = phase;
	}

	/**
	 * A string with no internal use, can be used for the map name example
	 *
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#EXTRA
	 */
	public static String getExtra() {
		return extra;
	}

	/**
	 * A string with no internal use, can be used for the map name example
	 *
	 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#EXTRA
	 */
	public static void setExtra(@Nullable String extra) {
		BridgeHelper.extra = extra;
	}

	public static void updateServiceInfo() {
		CloudWrapper.getInstance().updateServiceInfo();
	}

	@Nonnull
	@Deprecated
	public static ServiceInfo getServiceInfo() {
		return CloudWrapper.getInstance().getServiceInfo();
	}

	private BridgeHelper() {}

}
