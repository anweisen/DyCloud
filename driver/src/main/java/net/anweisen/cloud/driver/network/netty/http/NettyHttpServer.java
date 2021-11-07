package net.anweisen.cloud.driver.network.netty.http;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.http.HttpServer;
import net.anweisen.cloud.driver.network.http.auth.HttpAuthRegistry;
import net.anweisen.cloud.driver.network.http.handler.HttpHandlerRegistry;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpServer implements HttpServer, LoggingApiUser {

	protected final EventLoopGroup bossEventLoopGroup = NettyUtils.newEventLoopGroup();
	protected final EventLoopGroup workerEventLoopGroup = NettyUtils.newEventLoopGroup();

	protected final HttpHandlerRegistry handlerRegistry = new HttpHandlerRegistry();
	protected final HttpAuthRegistry authRegistry = new HttpAuthRegistry();

	@Override
	public void addListener(@Nonnull HostAndPort address) {
		Preconditions.checkNotNull(address, "Address cannot be null");

		try {
			new ServerBootstrap()
				.group(bossEventLoopGroup, workerEventLoopGroup)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.IP_TOS, 24)
				.childOption(ChannelOption.AUTO_READ, true)
				.childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
				.channelFactory(NettyUtils.getServerChannelFactory())
				.childHandler(new NettyHttpServerInitializer(this, address))
				.bind(address.getHost(), address.getPort())
				.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
				.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
				.sync()
				.channel();

			info("Http server listening on {}!", address);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		try {
			bossEventLoopGroup.shutdownGracefully();
			workerEventLoopGroup.shutdownGracefully();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Nonnull
	@Override
	public HttpHandlerRegistry getHandlerRegistry() {
		return handlerRegistry;
	}

	@Nonnull
	@Override
	public HttpAuthRegistry getAuthRegistry() {
		return authRegistry;
	}
}
