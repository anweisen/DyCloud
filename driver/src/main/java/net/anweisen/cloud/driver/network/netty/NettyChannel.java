package net.anweisen.cloud.driver.network.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.SocketChannelHandler;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.utilities.common.logging.LogLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyChannel implements SocketChannel {

	protected final HostAndPort serverAddress, clientAddress;
	protected final boolean client;
	protected final Channel channel;

	protected SocketChannelHandler handler;

	public NettyChannel(@Nonnull Channel channel, @Nullable SocketChannelHandler handler, @Nonnull HostAndPort serverAddress, @Nonnull HostAndPort clientAddress, boolean client) {
		Preconditions.checkNotNull(channel, "Channel cannot be null");
		Preconditions.checkNotNull(serverAddress, "ServerAddress cannot be null");
		Preconditions.checkNotNull(clientAddress, "ClientAddress cannot be null");

		this.channel = channel;
		this.handler = handler;
		this.serverAddress = serverAddress;
		this.clientAddress = clientAddress;
		this.client = client;
	}

	@Override
	public void sendPacket(@Nonnull Packet packet) {
		Preconditions.checkNotNull(packet, "Packet cannot be null");

		if (channel.eventLoop().inEventLoop()) {
			writePacket(packet);
		} else {
			channel.eventLoop().execute(() -> writePacket(packet));
		}
	}

	@Override
	public void sendPacketSync(@Nonnull Packet packet) {
		Preconditions.checkNotNull(packet, "Packet cannot be null");

		ChannelFuture future = writePacket(packet);
		if (future != null)
			future.syncUninterruptibly();
	}

	private ChannelFuture writePacket(@Nonnull Packet packet) {

		if (packet.isShowDebug() && CloudDriver.getInstance() != null && CloudDriver.getInstance().getLogger().isLevelEnabled(LogLevel.DEBUG)) {
			CloudDriver.getInstance().getLogger().debug(
				"Sending packet on channel {} with id {}, header={};body={}",
				packet.getChannel(),
				packet.getUniqueId(),
				packet.getHeader().toJson(),
				packet.getBuffer() != null ? packet.getBuffer().readableBytes() : 0
			);
		}

		return channel.writeAndFlush(packet);
	}

	@Override
	public void close() throws Exception {
		channel.close();
	}

	@Override
	public boolean isActive() {
		return channel.isActive();
	}

	@Override
	public boolean isWritable() {
		return channel.isWritable();
	}

	@Nullable
	@Override
	public SocketChannelHandler getHandler() {
		return handler;
	}

	@Override
	public void setHandler(SocketChannelHandler handler) {
		this.handler = handler;
	}

	@Nonnull
	@Override
	public HostAndPort getServerAddress() {
		return serverAddress;
	}

	@Nonnull
	@Override
	public HostAndPort getClientAddress() {
		return clientAddress;
	}

	@Override
	public boolean isClientSide() {
		return client;
	}
}
