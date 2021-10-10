package net.anweisen.cloud.master.network.listener;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.event.player.*;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket;
import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.connection.DefaultPlayerConnection;
import net.anweisen.cloud.driver.player.settings.DefaultPlayerSettings;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerEventListener implements PacketListener, LoggingApiUser {

	// TODO parallel processing not allowed, actually only needed for proxy actions?
	@Override
	public synchronized void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();
		PacketBuffer buffer = packet.getBuffer();

		PlayerEventPayload payload = buffer.readEnum(PlayerEventPayload.class);

		CloudService service = cloud.getServiceManager().getServiceByChannel(channel);
		Preconditions.checkNotNull(service, "Service from which a " + payload + " packet was received is not registered");
		ServiceInfo serviceInfo = service.getInfo();

		if (payload.getType() == ServiceType.PROXY) {
			UUID playerUniqueId = buffer.readUniqueId();
			debug("{} '{}' -> {}", payload, serviceInfo.getName(), playerUniqueId);

			switch (payload) {
				case PROXY_LOGIN_REQUEST: {
					CloudPlayer player = cloud.getPlayerManager().loginPlayer(
						playerUniqueId, buffer.readString(), buffer.readObject(DefaultPlayerConnection.class), serviceInfo,
						cancelReason -> channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeOptionalObject(null).writeOptionalString(cancelReason)))
					);
					if (player == null) return;

					channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeOptionalObject((SerializableObject) player).writeOptionalString(null)));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forProxyLoginRequest(player), channel);
					break;
				}
				case PROXY_LOGIN_SUCCESS: {
					PlayerSettings settings = buffer.readObject(DefaultPlayerSettings.class);

					CloudPlayer player = findPlayer(playerUniqueId);
					info("Player[name={} uuid={}] successfully joined the network on '{}'", player.getName(), player.getUniqueId(), serviceInfo.getName());

					player.setSettings(settings);

					cloud.getEventManager().callEvent(new PlayerProxyLoginSuccessEvent(player));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forProxyLoginSuccess(playerUniqueId, settings));
					break;
				}
				case PROXY_SERVER_CONNECT_REQUEST: {
					UUID targetUniqueId = buffer.readUniqueId();

					CloudPlayer player = findPlayer(playerUniqueId);
					ServiceInfo target = findService(targetUniqueId);
					info("Player[name={} uuid={}] requested to connect to the Minecraftserver '{}' on '{}'", player.getName(), player.getUniqueId(), target.getName(), serviceInfo.getName());

					cloud.getEventManager().callEvent(new PlayerProxyServerConnectRequestEvent(player, target));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forProxyServerConnectRequest(playerUniqueId, targetUniqueId));
					break;
				}
				case PROXY_SERVER_SWITCH: {
					UUID fromUniqueId = buffer.readUniqueId();
					UUID toUniqueId = buffer.readUniqueId();

					CloudPlayer player = findPlayer(playerUniqueId);
					ServiceInfo from = findService(fromUniqueId);
					ServiceInfo to = findService(toUniqueId);
					info("Player[name={} uuid={}] switched from '{}' '{}'", from.getName(), to.getName());

					player.setCurrentServer(toUniqueId);

					cloud.getEventManager().callEvent(new PlayerProxyServerSwitchEvent(player, from, to));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forProxyServerSwitch(playerUniqueId, fromUniqueId, toUniqueId));
					break;
				}
				case PROXY_DISCONNECT: {
					CloudPlayer player = findPlayer(playerUniqueId);
					info("Player[name={} uuid={}] left the network on '{}'", player.getName(), player.getUniqueId(), serviceInfo.getName());

					player.setCurrentServer(null);
					player.setOnline(false);
					player.setLastOnlineTime(System.currentTimeMillis());

					cloud.getPlayerManager().unregisterPlayer(player.getUniqueId());
					cloud.getEventManager().callEvent(new PlayerProxyDisconnectEvent(player));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forProxyDisconnect(playerUniqueId));
					player.update();
					break;
				}
			}
		} else if (payload.getType() == ServiceType.SERVER) {
			UUID playerUniqueId = buffer.readUniqueId();
			debug("{} '{}' -> {}", payload, serviceInfo.getName(), playerUniqueId);

			switch (payload) {
				case SERVER_LOGIN_REQUEST: {
					CloudPlayer player = findPlayer(playerUniqueId);

					cloud.getEventManager().callEvent(new PlayerServerLoginRequestEvent(player, serviceInfo));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forServerLoginRequest(playerUniqueId, serviceInfo.getUniqueId()));
					break;
				}
				case SERVER_LOGIN_SUCCESS: {
					CloudPlayer player = findPlayer(playerUniqueId);

					cloud.getEventManager().callEvent(new PlayerServerLoginSuccessEvent(player, serviceInfo));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forServerLoginSuccess(playerUniqueId, serviceInfo.getUniqueId()));
					break;
				}
				case SERVER_DISCONNECT: {
					CloudPlayer player = cloud.getPlayerManager().getOnlinePlayerByUniqueId(playerUniqueId); // Dont use findPlayer(...) here as we use an optional nullable value

					cloud.getEventManager().callEvent(new PlayerServerDisconnectEvent(player, serviceInfo, playerUniqueId));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forServerDisconnect(playerUniqueId, serviceInfo.getUniqueId()));
					break;
				}
			}
		} else if (payload.getType() == null) {
			UUID playerUniqueId = buffer.readUniqueId();
			debug("{} '{}' -> {}", payload, serviceInfo.getName(), playerUniqueId);
			CloudPlayer player = findPlayer(playerUniqueId);

			switch (payload) {
				case PLAYER_SETTINGS_CHANGE: {
					PlayerSettings from = player.getSettings();
					PlayerSettings to = buffer.readObject(DefaultPlayerSettings.class);

					player.setSettings(to);

					cloud.getEventManager().callEvent(new PlayerSettingsChangeEvent(player, from, to));
					cloud.getSocketComponent().sendPacket(PlayerEventPacket.forPlayerSettingsChange(playerUniqueId, to));
					break;
				}
			}
		}
	}

	@Nonnull
	private CloudPlayer findPlayer(@Nonnull UUID uniqueId) {
		CloudPlayer player = CloudMaster.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(uniqueId);
		Preconditions.checkNotNull(player, "OnlinePlayer for " + uniqueId + " is null");
		return player;
	}

	@Nonnull
	private ServiceInfo findService(@Nonnull UUID uniqueId) {
		ServiceInfo service = CloudMaster.getInstance().getServiceManager().getServiceInfoByUniqueId(uniqueId);
		Preconditions.checkNotNull(service, "ServiceInfo for " + uniqueId + " is null");
		return service;
	}

}
