package net.anweisen.cloud.modules.bridge.helper;

import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.bukkit.listener.BukkitCloudListener;
import net.anweisen.cloud.modules.bridge.bungee.listener.BungeeCloudListener;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.anweisen.cloud.wrapper.event.service.ServiceInfoConfigureEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BridgeHelper {

	/**
	 *  @see BungeeCloudListener#onInfoConfigure(ServiceInfoConfigureEvent)
	 *  @see BukkitCloudListener#onInfoConfigure(ServiceInfoConfigureEvent)
	 */
	private static int maxPlayers, lastOnlineCount;
	private static String motd, status, extra;

	public static int getMaxPlayers() {
		return maxPlayers;
	}

	public static void setMaxPlayers(int maxPlayers) {
		BridgeHelper.maxPlayers = maxPlayers;
	}

	public static int getLastOnlineCount() {
		return lastOnlineCount;
	}

	public static void setOnlineCount(int lastOnlineCount) {
		BridgeHelper.lastOnlineCount = lastOnlineCount;
	}

	public static String getMotd() {
		return motd;
	}

	public static void setMotd(@Nonnull String motd) {
		BridgeHelper.motd = motd;
	}

	public static String getStatus() {
		return status;
	}

	public static void setStatus(@Nullable String status) {
		BridgeHelper.status = status;
	}

	public static String getExtra() {
		return extra;
	}

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
