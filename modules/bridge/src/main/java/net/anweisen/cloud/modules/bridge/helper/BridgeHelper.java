package net.anweisen.cloud.modules.bridge.helper;

import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.bukkit.listener.BukkitCloudListener;
import net.anweisen.cloud.modules.bridge.bungee.listener.BungeeCloudListener;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.anweisen.cloud.wrapper.event.service.ServiceInfoConfigureEvent;
import net.anweisen.utilities.common.collection.pair.Tuple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

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

	@Nonnull
	public static Tuple<CloudPlayer, String> sendProxyLoginRequestPacket(@Nonnull UUID playerUniqueId, @Nonnull String playerName, @Nonnull PlayerConnection playerConnection) {
		return Optional.ofNullable(
				CloudWrapper.getInstance().getSocketComponent().getFirstChannel().sendPacketQuery(PlayerEventPacket.forProxyLoginRequest(playerUniqueId, playerName, playerConnection))
			)
			.map(packet -> Tuple.<CloudPlayer, String>of(packet.getBuffer().readOptionalObject(DefaultCloudPlayer.class), packet.getBuffer().readOptionalString()))
			.orElse(Tuple.empty());
	}

	public static void sendProxyLoginSuccessPacket(@Nonnull UUID playerUniqueId, @Nonnull PlayerSettings playerSettings) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forProxyLoginSuccess(playerUniqueId, playerSettings));
	}

	public static void sendProxyServerConnectRequestPacket(@Nonnull UUID playerUniqueId, @Nonnull UUID targetUniqueId) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forProxyServerConnectRequest(playerUniqueId, targetUniqueId));
	}

	public static void sendProxyServerSwitchPacket(@Nonnull UUID playerUniqueId, @Nonnull UUID fromUniqueId, @Nonnull UUID toUniqueId) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forProxyServerSwitch(playerUniqueId, fromUniqueId, toUniqueId));
	}

	public static void sendProxyDisconnectPacket(@Nonnull UUID playerUniqueId) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forProxyDisconnect(playerUniqueId));
	}

	public static void sendServerLoginRequestPacket(@Nonnull UUID playerUniqueId) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forServerLoginRequest(playerUniqueId));
	}

	public static void sendServerLoginSuccessPacket(@Nonnull UUID playerUniqueId) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forServerLoginSuccess(playerUniqueId));
	}

	public static void sendServerDisconnectPacket(@Nonnull UUID playerUniqueId) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forServerDisconnect(playerUniqueId));
	}

	public static void sendPlayerSettingsChangePacket(@Nonnull UUID playerUniqueId, @Nonnull PlayerSettings settings) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forPlayerSettingsChange(playerUniqueId, settings));
	}

	private BridgeHelper() {}

}
