package net.anweisen.cloud.driver.network.listener;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.player.*;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
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
		Buffer buffer = packet.getBuffer();

		PlayerEventType type = buffer.readEnumConstant(PlayerEventType.class);
		if (type == PlayerEventType.PROXY_LOGIN_REQUEST) {
			CloudPlayer player = buffer.readObject(DefaultCloudPlayer.class);
			cloud.getPlayerManager().registerPlayer(player);
			cloud.getEventManager().callEvent(new PlayerProxyLoginRequestEvent(player));
			return;
		}

		UUID uuid = buffer.readUUID();
		CloudPlayer player = cloud.getPlayerManager().getOnlinePlayerByUniqueId(uuid);
		if (type == PlayerEventType.SERVER_DISCONNECT) {
			String name = buffer.readString();
			ServiceInfo service = cloud.getServiceManager().getServiceInfoByUniqueId(buffer.readUUID());
			cloud.getEventManager().callEvent(new PlayerServerDisconnectEvent(player, service, name, uuid));
			return;
		}

		Preconditions.checkNotNull(player, "Online CloudPlayer for " + uuid + " is null");
		switch (type) {
			case PROXY_LOGIN_SUCCESS: {
				cloud.getEventManager().callEvent(new PlayerProxyLoginSuccessEvent(player));
				break;
			}
			case PROXY_SERVER_CONNECT_REQUEST: {
				ServiceInfo service = cloud.getServiceManager().getServiceInfoByUniqueId(buffer.readUUID());
				cloud.getEventManager().callEvent(new PlayerProxyServerConnectRequestEvent(player, service));
				break;
			}
			case PROXY_SERVER_SWITCH: {
				ServiceInfo from = cloud.getServiceManager().getServiceInfoByUniqueId(buffer.readUUID());
				ServiceInfo to = cloud.getServiceManager().getServiceInfoByUniqueId(buffer.readUUID());
				player.setCurrentServer(to);
				cloud.getEventManager().callEvent(new PlayerProxyServerSwitchEvent(player, from, to));
				break;
			}
			case PROXY_DISCONNECT: {
				player.setOnline(false);
				cloud.getPlayerManager().unregisterPlayer(uuid);
				cloud.getEventManager().callEvent(new PlayerProxyDisconnectEvent(player));
				break;
			}
			case SERVER_LOGIN_REQUEST: {
				ServiceInfo service = cloud.getServiceManager().getServiceInfoByUniqueId(buffer.readUUID());
				cloud.getEventManager().callEvent(new PlayerServerLoginRequestEvent(player, service));
				break;
			}
		}

	}
}
