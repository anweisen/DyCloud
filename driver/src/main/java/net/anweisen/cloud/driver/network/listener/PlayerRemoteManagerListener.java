package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.player.PlayerUpdateEvent;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerRemoteManagerPacket.PlayerRemoteManagerPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.defaults.RemotePlayerManager;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerRemoteManagerListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudDriver cloud = CloudDriver.getInstance();
		PacketBuffer buffer = packet.getBuffer();

		PlayerRemoteManagerPayload payload = buffer.readEnum(PlayerRemoteManagerPayload.class);
		switch (payload) {
			case UPDATE_ONLINE_PLAYER: {
				RemotePlayerManager manager = (RemotePlayerManager) cloud.getPlayerManager();
				CloudPlayer player = buffer.readObject(DefaultCloudPlayer.class);
				manager.handleOnlinePlayerUpdate(player);
				cloud.getEventManager().callEvent(new PlayerUpdateEvent(player));
				break;
			}
		}
	}
}
