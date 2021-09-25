package net.anweisen.cloud.driver.network.listener;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.player.*;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.settings.DefaultPlayerSettings;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerEventListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudDriver cloud = CloudDriver.getInstance();
		PacketBuffer buffer = packet.getBuffer();

		PlayerEventPayload payload = buffer.readEnum(PlayerEventPayload.class);

		if (payload == PlayerEventPayload.PROXY_LOGIN_REQUEST) {
			CloudPlayer player = buffer.readObject(DefaultCloudPlayer.class);
			cloud.getPlayerManager().registerPlayer(player);
			cloud.getEventManager().callEvent(new PlayerProxyLoginRequestEvent(player));
			return;
		}

		UUID uuid = buffer.readUniqueId();
		CloudPlayer player = cloud.getPlayerManager().getOnlinePlayerByUniqueId(uuid);

		if (payload == PlayerEventPayload.SERVER_DISCONNECT) {
			ServiceInfo service = findService(buffer.readUniqueId());
			cloud.getEventManager().callEvent(new PlayerServerDisconnectEvent(player, service, uuid));
			return;
		}

		Preconditions.checkNotNull(player, "Online CloudPlayer for " + uuid + " is null");
		switch (payload) {
			case PROXY_LOGIN_SUCCESS: {
				PlayerSettings settings = buffer.readObject(DefaultPlayerSettings.class);
				player.setSettings(settings);
				cloud.getEventManager().callEvent(new PlayerProxyLoginSuccessEvent(player));
				break;
			}
			case PROXY_SERVER_CONNECT_REQUEST: {
				ServiceInfo service = findService(buffer.readUniqueId());
				cloud.getEventManager().callEvent(new PlayerProxyServerConnectRequestEvent(player, service));
				break;
			}
			case PROXY_SERVER_SWITCH: {
				ServiceInfo from = findService(buffer.readUniqueId());
				ServiceInfo to = findService(buffer.readUniqueId());
				player.setCurrentServer(to.getUniqueId());
				cloud.getEventManager().callEvent(new PlayerProxyServerSwitchEvent(player, from, to));
				break;
			}
			case PROXY_DISCONNECT: { // TODO this does not feel right
				player.setOnline(false);
				cloud.getPlayerManager().unregisterPlayer(uuid);
				cloud.getEventManager().callEvent(new PlayerProxyDisconnectEvent(player));
				break;
			}
			case SERVER_LOGIN_SUCCESS: {
				ServiceInfo service = findService(buffer.readUniqueId());
				cloud.getEventManager().callEvent(new PlayerServerLoginSuccessEvent(player, service));
				break;
			}
			case SERVER_LOGIN_REQUEST: {
				ServiceInfo service = findService(buffer.readUniqueId());
				cloud.getEventManager().callEvent(new PlayerServerLoginRequestEvent(player, service));
				break;
			}
			case PLAYER_SETTINGS_CHANGE: {
				PlayerSettings from = player.getSettings();
				PlayerSettings to = buffer.readObject(DefaultPlayerSettings.class);
				player.setSettings(to);
				cloud.getEventManager().callEvent(new PlayerSettingsChangeEvent(player, from, to));
				break;
			}
		}

	}

	@Nonnull
	private ServiceInfo findService(@Nonnull UUID uniqueId) {
		ServiceInfo service = CloudDriver.getInstance().getServiceManager().getServiceInfoByUniqueId(uniqueId);
		Preconditions.checkNotNull(service, "ServiceInfo for " + uniqueId + " is null");
		return service;
	}
}
