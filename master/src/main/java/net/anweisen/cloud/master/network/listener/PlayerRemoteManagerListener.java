package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerRemoteManagerPacket.PlayerRemoteManagerType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.PlayerManager;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudOfflinePlayer;

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
		Buffer buffer = packet.getBuffer();
		PlayerManager manager = CloudDriver.getInstance().getPlayerManager();

		PlayerRemoteManagerType type = buffer.readEnumConstant(PlayerRemoteManagerType.class);

		switch (type) {
			case GET_REGISTERED_COUNT: {
				debug("{}", type);
				long count = manager.getRegisteredPlayerCount();
				channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeLong(count)));
				break;
			}
			case GET_REGISTERED_PLAYERS: {
				debug("{}", type);
				@SuppressWarnings("unchecked")
				Collection<? extends SerializableObject> players = (Collection<? extends SerializableObject>) (Collection<?>) manager.getRegisteredPlayers(); // Cannot cast directly to <? extends SerializableObject>
				channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeObjectCollection(players)));
				break;
			}
			case GET_OFFLINE_PLAYER_BY_UUID: {
				UUID uuid = buffer.readUUID();
				debug("{} -> {}", type, uuid);
				SerializableObject player = (SerializableObject) manager.getOfflinePlayerByUniqueId(uuid);
				channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeOptionalObject(player)));
				break;
			}
			case GET_OFFLINE_PLAYER_BY_NAME: {
				String name = buffer.readString();
				debug("{} -> {}", type, name);
				SerializableObject player = (SerializableObject) manager.getOfflinePlayerByName(name);
				channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeOptionalObject(player)));
				break;
			}
			case SAVE_OFFLINE_PLAYER: {
				CloudOfflinePlayer player = buffer.readObject(DefaultCloudOfflinePlayer.class);
				debug("{} -> {}", type, player);
				manager.saveOfflinePlayer(player);
				break;
			}
		}

	}

}
