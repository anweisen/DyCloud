package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.Packet;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SocketChannelServerHandler implements SocketChannelHandler {

	@Override
	public void handleChannelInitialize(@Nonnull SocketChannel channel) throws Exception {

		// TODO Security: IP Whitelist check

		CloudDriver.getInstance().getLogger().info("Channel [client={} server={}] was successfully connected", channel.getClientAddress(), channel.getServerAddress());

	}

	@Override
	public boolean handlePacketReceive(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		return false;
	}

	@Override
	public void handleChannelClose(@Nonnull SocketChannel channel) throws Exception {

		CloudDriver.getInstance().getLogger().info("Channel [client={} server={}] was closed", channel.getClientAddress(), channel.getServerAddress());

	}
}
