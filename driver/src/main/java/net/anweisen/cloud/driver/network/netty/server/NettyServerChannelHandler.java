package net.anweisen.cloud.driver.network.netty.server;

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
public class NettyServerChannelHandler extends NettyChannelHandler {

	protected final NettySocketServer server;
	protected final HostAndPort address;

	public NettyServerChannelHandler(@Nonnull NettySocketServer server, @Nonnull HostAndPort address) {
		this.server = server;
		this.address = address;
	}

	@Override
	public void channelActive(@Nonnull ChannelHandlerContext context) throws Exception {
		channel = new NettyChannel(
			context.channel(),
			server.getHandler().get(),
			address, // we are the server
			HostAndPort.fromSocketAddress(context.channel().remoteAddress()),
			false
		);

		getChannels().add(channel);

		if (channel.getHandler() != null)
			channel.getHandler().handleChannelInitialize(channel);
	}


	@Nonnull
	@Override
	protected Executor getPacketDispatcher() {
		return server.getPacketDispatcher();
	}

	@Nonnull
	@Override
	protected Collection<SocketChannel> getChannels() {
		return server.getChannels();
	}

	@Nonnull
	@Override
	protected PacketListenerRegistry getListenerRegistry() {
		return server.getListenerRegistry();
	}
}
