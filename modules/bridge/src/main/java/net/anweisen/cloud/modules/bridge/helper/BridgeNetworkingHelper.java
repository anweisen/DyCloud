package net.anweisen.cloud.modules.bridge.helper;

import net.anweisen.cloud.driver.network.packet.def.CommandSystemPacket;
import net.anweisen.cloud.driver.network.packet.def.CommandSystemPacket.CommandSystemPayload;
import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.anweisen.utility.common.collection.pair.Tuple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BridgeNetworkingHelper {

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

	public static void sendProxyRemovePacket(@Nonnull UUID playerUniqueId) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(PlayerEventPacket.forProxyRemove(playerUniqueId));
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

	@Nullable
	public static List<String> sendCommandCompletePacket(@Nonnull UUID playerUniqueId, @Nonnull String input) {
		return Optional.ofNullable(
			CloudWrapper.getInstance().getSocketComponent().getFirstChannel().sendPacketQuery(new CommandSystemPacket(CommandSystemPayload.COMPLETE, playerUniqueId, input))
		).map(packet -> packet.getBuffer().readStringCollection())
		.orElse(null);
	}

	public static void sendCommandExecutePacket(@Nonnull UUID playerUniqueId, @Nonnull String input) {
		CloudWrapper.getInstance().getSocketComponent().sendPacket(new CommandSystemPacket(CommandSystemPayload.EXECUTE, playerUniqueId, input));
	}

	private BridgeNetworkingHelper() {}
}
