package net.anweisen.cloud.driver.network.netty.server;

import io.netty.channel.ChannelHandlerContext;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.network.netty.NettyChannel;
import net.anweisen.cloud.driver.network.netty.NettyChannelHandler;

import javax.annotation.Nonnull;

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
			server.getHandlerSupplier().get(),
			address, // we are the server
			HostAndPort.fromSocketAddress(context.channel().remoteAddress()),
			false
		);

		server.getChannels().add(channel);

		if (channel.getHandler() != null)
			channel.getHandler().handleChannelInitialize(channel);
	}

	@Nonnull
	@Override
	public SocketComponent getComponent() {
		return server;
	}
}
