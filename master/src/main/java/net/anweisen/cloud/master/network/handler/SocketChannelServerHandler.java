package net.anweisen.cloud.master.network.handler;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.network.PacketReceiveEvent;
import net.anweisen.cloud.driver.event.network.SocketChannelConnectEvent;
import net.anweisen.cloud.driver.network.InternalQueryResponseManager;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.packets.ConfigInitPacket;
import net.anweisen.cloud.master.CloudMaster;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SocketChannelServerHandler implements SocketChannelHandler {

	@Override
	public void handleChannelInitialize(@Nonnull SocketChannel channel) throws Exception {
		CloudDriver driver = CloudDriver.getInstance();

		if (!inWhitelist(channel)) {
			try {
				channel.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}

		SocketChannelConnectEvent event = driver.getEventManager().callEvent(new SocketChannelConnectEvent(channel));
		if (event.isCancelled()) {
			try {
				channel.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}

		driver.getLogger().info("Channel[client={} server={}] was successfully connected", channel.getClientAddress(), channel.getServerAddress());

		channel.sendPacket(ConfigInitPacket.create());

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

		driver.getEventManager().callEvent(new SocketChannelConnectEvent(channel));
		driver.getLogger().info("Channel[client={} server={}] was closed", channel.getClientAddress(), channel.getServerAddress());
	}

	protected boolean inWhitelist(@Nonnull SocketChannel channel) {
		for (String whitelistedIp : CloudMaster.getInstance().getConfig().getIpWhitelist()) {
			if (channel.getClientAddress().getHost().equals(whitelistedIp))
				return true;
		}

		return false;
	}

}
