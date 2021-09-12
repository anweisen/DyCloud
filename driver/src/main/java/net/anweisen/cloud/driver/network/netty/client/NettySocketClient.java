package net.anweisen.cloud.driver.network.netty.client;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.DefaultSocketComponent;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.SocketClient;
import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.utilities.common.collection.WrappedException;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettySocketClient extends DefaultSocketComponent implements SocketClient, LoggingApiUser {

	private static final int CONNECTION_TIMEOUT_MILLIS = 5_000;

	protected final EventLoopGroup eventLoopGroup = NettyUtils.newEventLoopGroup();

	public NettySocketClient(@Nonnull Supplier<SocketChannelHandler> handler) {
		super(handler);
	}

	@Override
	public void connect(@Nonnull HostAndPort address) {
		Preconditions.checkNotNull(address, "Address cannot be null");

		try {
			Channel channel = doConnect(address);
			channel.closeFuture().addListener(future -> {
				reconnect(address);
			});
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			eventLoopGroup.shutdownGracefully();
			throw WrappedException.rethrow(ex);
		}
	}

	private void reconnect(@Nonnull HostAndPort address) {
		warn("Got disconnected from the master socket! Reconnecting..");

		try {
			Channel channel = doConnect(address);
			channel.closeFuture().addListener(future -> {
				reconnect(address);
			});
		} catch (Exception ex) {
			error("Unable to reconnect to master socket", ex);

			// We wait 1 second until we try to reconnect
			try {
				Thread.sleep(1000);
			} catch (InterruptedException exInterrupted) {
				exInterrupted.printStackTrace();
			}

			reconnect(address);
		}
	}

	private Channel doConnect(@Nonnull HostAndPort address) throws Exception {
		return new Bootstrap()
			.group(eventLoopGroup)
			.option(ChannelOption.AUTO_READ, true)
			.option(ChannelOption.IP_TOS, 24)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MILLIS)
			.channelFactory(NettyUtils.getClientChannelFactory())
			.handler(new NettyClientInitializer(this, address))
			.connect(address.getHost(), address.getPort())
			.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
			.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
			.sync().channel();
	}

	@Override
	public void shutdown() {
		try {
			eventLoopGroup.shutdownGracefully().sync();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Nonnull
	@Override
	public Collection<SocketChannel> getChannels() {
		return channels;
	}

}
