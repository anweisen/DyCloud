package net.anweisen.cloud.master.network.listener;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.event.player.*;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.data.PlayerProxyConnectionData;
import net.anweisen.cloud.driver.player.data.PlayerServerConnectionData;
import net.anweisen.cloud.driver.player.data.UnspecifiedPlayerConnectionData;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerEventListener implements PacketListener, LoggingApiUser {

	// TODO parallel processing not allowed, actually only needed for proxy actions?
	@Override
	public synchronized void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();
		Buffer buffer = packet.getBuffer();

		PlayerEventType type = buffer.readEnumConstant(PlayerEventType.class);

		CloudService service = cloud.getServiceManager().getServiceByChannel(channel);
		Preconditions.checkNotNull(service, "Service from which a " + type + " packet was received is not registered");
		ServiceInfo serviceInfo = service.getInfo();

		if (type.getType() == ServiceType.PROXY) {
			PlayerProxyConnectionData playerConnection = buffer.readObject(PlayerProxyConnectionData.class);
			debug("{} '{}' -> {}", type, serviceInfo.getName(), playerConnection);

			switch (type) {
				case PROXY_LOGIN_REQUEST: {
					info("Player[name={} uuid={}] requested to login on Proxy '{}'", playerConnection.getName(), playerConnection.getUniqueId(), serviceInfo.getName());
					CloudPlayer player = cloud.getPlayerManager().getOnlinePlayerByUniqueId(playerConnection.getUniqueId());
					final String reason;
					if (player != null) {
						debug("Player[name={} uuid={}] is already connected to the network!", playerConnection.getName(), playerConnection.getUniqueId());
						reason = "§cAlready connected to the network";
					} else {
						player = cloud.getPlayerManager().registerOnlinePlayer(playerConnection, serviceInfo);
						PlayerProxyLoginRequestEvent event = cloud.getEventManager().callEvent(new PlayerProxyLoginRequestEvent(player));
						reason = event.isCancelled() ? (event.getCancelledReason() != null ? event.getCancelledReason() : "§cNo kick reason given") : null;
					}

					channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeObject((SerializableObject) player).writeOptionalString(reason)));
					break;
				}
				case PROXY_LOGIN_SUCCESS: {
					info("Player[name={} uuid={}] successfully joined the network on '{}'", playerConnection.getName(), playerConnection.getUniqueId(), serviceInfo.getName());
					CloudPlayer player = findPlayer(playerConnection);
					cloud.getEventManager().callEvent(new PlayerProxyLoginSuccessEvent(player));
					break;
				}
				case PROXY_SERVER_CONNECT_REQUEST: {
					ServiceInfo target = buffer.readObject(ServiceInfo.class);
					info("Player[name={} uuid={}] requested to connect to the Minecraftserver '{}' on '{}'", playerConnection.getName(), playerConnection.getUniqueId(), target.getName(), serviceInfo.getName());
					CloudPlayer player = findPlayer(playerConnection);
					cloud.getEventManager().callEvent(new PlayerProxyServerConnectRequestEvent(player, target));
					break;
				}
				case PROXY_SERVER_SWITCH: {
					ServiceInfo from = buffer.readObject(ServiceInfo.class);
					ServiceInfo to = buffer.readObject(ServiceInfo.class);
					info("Player[name={} uuid={}] switched from '{}' '{}'", from.getName(), to.getName());
					CloudPlayer player = findPlayer(playerConnection);
					player.setCurrentServer(to);
					cloud.getEventManager().callEvent(new PlayerProxyServerSwitchEvent(player, from, to));
					break;
				}
				case PROXY_DISCONNECT: {
					info("Player[name={} uuid={}] left the network on '{}'", playerConnection.getName(), playerConnection.getUniqueId(), serviceInfo.getName());
					CloudPlayer player = findPlayer(playerConnection);
					player.setOnline(false);
					player.setLastOnlineTime(System.currentTimeMillis());
					cloud.getPlayerManager().unregisterOnlinePlayer(player);
					cloud.getEventManager().callEvent(new PlayerProxyDisconnectEvent(player));
					player.save();
					break;
				}
			}
		} else if (type.getType() == ServiceType.SERVER) {
			PlayerServerConnectionData playerConnection = buffer.readObject(PlayerServerConnectionData.class);
			debug("{} '{}' -> {}", type, serviceInfo.getName(), playerConnection);

			switch (type) {
				case SERVER_LOGIN_REQUEST: {
					CloudPlayer player = findPlayer(playerConnection);
					cloud.getEventManager().callEvent(new PlayerServerLoginRequestEvent(player, serviceInfo));
					break;
				}
				case SERVER_LOGIN_SUCCESS: {
					CloudPlayer player = findPlayer(playerConnection);
					player.setServerConnectionData(playerConnection);
					cloud.getEventManager().callEvent(new PlayerServerLoginSuccessEvent(player, serviceInfo));
					break;
				}
				case SERVER_DISCONNECT: {
					CloudPlayer player = cloud.getPlayerManager().getOnlinePlayerByUniqueId(playerConnection.getUniqueId());
					cloud.getEventManager().callEvent(new PlayerServerDisconnectEvent(player, serviceInfo));
					break;
				}
			}
		}
	}

	@Nonnull
	private CloudPlayer findPlayer(@Nonnull UnspecifiedPlayerConnectionData playerConnection) {
		CloudPlayer player = CloudMaster.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(playerConnection.getUniqueId());
		Preconditions.checkNotNull(player, "OnlinePlayer for " + playerConnection.getName() + " is null");
		return player;
	}

}
