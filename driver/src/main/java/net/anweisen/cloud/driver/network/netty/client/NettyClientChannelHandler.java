package net.anweisen.cloud.driver.network.netty.client;

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

		client.getChannels().add(channel);

		if (channel.getHandler() != null)
			channel.getHandler().handleChannelInitialize(channel);
	}

	@Nonnull
	@Override
	public SocketComponent getComponent() {
		return client;
	}
}
