package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerRemoteManagerPacket.PlayerRemoteManagerPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerManager;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudOfflinePlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerRemoteManagerListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		PacketBuffer buffer = packet.getBuffer();
		PlayerManager manager = CloudDriver.getInstance().getPlayerManager();

		PlayerRemoteManagerPayload payload = buffer.readEnum(PlayerRemoteManagerPayload.class);

		switch (payload) {
			case GET_REGISTERED_COUNT: {
				debug("{}", payload);
				long count = manager.getRegisteredPlayerCount();
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeLong(count)));
				break;
			}
			case GET_REGISTERED_PLAYERS: {
				debug("{}", payload);
				@SuppressWarnings("unchecked")
				Collection<? extends SerializableObject> players = (Collection<? extends SerializableObject>) (Collection<?>) manager.getRegisteredPlayers(); // Cannot cast directly to <? extends SerializableObject>
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeObjectCollection(players)));
				break;
			}
			case GET_OFFLINE_PLAYER_BY_UUID: {
				UUID uuid = buffer.readUniqueId();
				debug("PlayerRemoteManagerPayload.{} -> {}", payload, uuid);
				SerializableObject player = (SerializableObject) manager.getOfflinePlayerByUniqueId(uuid);
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeOptionalObject(player)));
				break;
			}
			case GET_OFFLINE_PLAYER_BY_NAME: {
				String name = buffer.readString();
				debug("PlayerRemoteManagerPayload.{} -> {}", payload, name);
				SerializableObject player = (SerializableObject) manager.getOfflinePlayerByName(name);
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeOptionalObject(player)));
				break;
			}
			case SAVE_OFFLINE_PLAYER: {
				CloudOfflinePlayer player = buffer.readObject(DefaultCloudOfflinePlayer.class);
				debug("PlayerRemoteManagerPayload.{} -> {}", payload, player);
				manager.saveOfflinePlayer(player);
				break;
			}
			case DELETE_OFFLINE_PLAYER: {
				UUID uuid = buffer.readUniqueId();
				debug("PlayerRemoteManagerPayload.{} -> {}", payload, uuid);
				manager.deleteOfflinePlayer(uuid);
				break;
			}
			case UPDATE_ONLINE_PLAYER: {
				CloudPlayer player = buffer.readObject(DefaultCloudPlayer.class);
				debug("PlayerRemoteManagerPayload.{} -> {}", payload, player);
				manager.updateOnlinePlayer(player);
				break;
			}
		}

	}

}
