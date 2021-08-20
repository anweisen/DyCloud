package net.anweisen.cloud.driver.network.netty.client;

import io.netty.channel.ChannelHandlerContext;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.netty.NettyChannel;
import net.anweisen.cloud.driver.network.netty.NettyChannelHandler;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyClientChannelHandler extends NettyChannelHandler {

	protected final NettySocketClient client;
	protected final HostAndPort address;

	public NettyClientChannelHandler(@Nonnull NettySocketClient client, @Nonnull HostAndPort address) {
		this.client = client;
		this.address = address;
	}

	@Override
	public void channelActive(@Nonnull ChannelHandlerContext context) throws Exception {
		channel = new NettyChannel(
			context.channel(),
			client.getHandlerSupplier().get(),
			HostAndPort.fromSocketAddress(context.channel().remoteAddress()),
			HostAndPort.fromSocketAddress(context.channel().localAddress()), // we are the client
			true
		);

		getChannels().add(channel);

		if (channel.getHandler() != null)
			channel.getHandler().handleChannelInitialize(channel);
	}


	@Nonnull
	@Override
	protected Executor getPacketDispatcher() {
		return client.getPacketDispatcher();
	}

	@Nonnull
	@Override
	protected Collection<SocketChannel> getChannels() {
		return client.getChannels();
	}

	@Nonnull
	@Override
	protected PacketListenerRegistry getListenerRegistry() {
		return client.getListenerRegistry();
	}
}
