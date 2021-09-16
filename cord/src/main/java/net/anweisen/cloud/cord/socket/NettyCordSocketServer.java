package net.anweisen.cloud.cord.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.netty.NettyUtils;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyCordSocketServer {

	protected final EventLoopGroup bossEventLoopGroup = NettyUtils.newEventLoopGroup();
	protected final EventLoopGroup workerEventLoopGroup = NettyUtils.newEventLoopGroup();

	protected Channel channel;

	public void init(@Nonnull HostAndPort address) throws Exception {

		channel = new ServerBootstrap()
				.group(bossEventLoopGroup, workerEventLoopGroup)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.IP_TOS, 24)
				.childOption(ChannelOption.AUTO_READ, true)
				.channelFactory(NettyUtils.getServerChannelFactory())
				.childHandler(new NettyCordServerInitializer(this))
				.bind(address.getHost(), address.getPort())
				.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
	    		.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
				.sync()
				.channel();

	}

	public void close() {

		channel.close();

		bossEventLoopGroup.shutdownGracefully();
		workerEventLoopGroup.shutdownGracefully();

	}

	@Nonnull
	public EventLoopGroup getBossEventLoopGroup() {
		return bossEventLoopGroup;
	}

	@Nonnull
	public EventLoopGroup getWorkerEventLoopGroup() {
		return workerEventLoopGroup;
	}
}
