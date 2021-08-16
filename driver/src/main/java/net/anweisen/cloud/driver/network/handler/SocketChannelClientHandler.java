package net.anweisen.cloud.driver.network.handler;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.network.PacketReceiveEvent;
import net.anweisen.cloud.driver.event.network.SocketChannelCloseEvent;
import net.anweisen.cloud.driver.event.network.SocketChannelConnectEvent;
import net.anweisen.cloud.driver.network.InternalQueryResponseManager;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SocketChannelClientHandler implements SocketChannelHandler {

	@Override
	public void handleChannelInitialize(@Nonnull SocketChannel channel) throws Exception {
		CloudDriver driver = CloudDriver.getInstance();

		SocketChannelConnectEvent event = driver.getEventManager().callEvent(new SocketChannelConnectEvent(channel));
		if (event.isCancelled()) {
			try {
				channel.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}

		driver.getLogger().info("Channel[client={} server={}] to network was successfully created", channel.getClientAddress(), channel.getServerAddress());
	}

	@Override
	public boolean handlePacketReceive(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		if (InternalQueryResponseManager.handleIncomingPacket(channel, packet))
			return false;

		return !CloudDriver.getInstance().getEventManager().callEvent(new PacketReceiveEvent(channel, packet)).isCancelled();
	}

	@Override
	public void handleChannelClose(@Nonnull SocketChannel channel) throws Exception {
		CloudDriver driver = CloudDriver.getInstance();

		driver.getEventManager().callEvent(new SocketChannelCloseEvent(channel));
		driver.getLogger().info("Channel[client={} server={}] to network was closed", channel.getClientAddress(), channel.getServerAddress());
	}
}
