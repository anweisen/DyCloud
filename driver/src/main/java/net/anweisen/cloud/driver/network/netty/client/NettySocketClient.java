package net.anweisen.cloud.driver.network.netty.client;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import net.anweisen.cloud.driver.network.*;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettySocketClient extends DefaultSocketComponent implements SocketClient {

	private static final int CONNECTION_TIMEOUT_MILLIS = 5_000;

	protected final EventLoopGroup eventLoopGroup = NettyUtils.newEventLoopGroup();

	public NettySocketClient(@Nonnull Supplier<SocketChannelHandler> handler) {
		super(handler);
	}

	@Override
	public void connect(@Nonnull HostAndPort address) {
		Preconditions.checkNotNull(address, "Address cannot be null");

		try {
			new Bootstrap()
				.group(this.eventLoopGroup)
				.option(ChannelOption.AUTO_READ, true)
				.option(ChannelOption.IP_TOS, 24)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MILLIS)
				.channelFactory(NettyUtils.getClientChannelFactory())
				.handler(new NettyClientInitializer(this, address))
				.connect(address.getHost(), address.getPort())
				.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
				.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
				.sync()
				.channel();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

	}

	@Nonnull
	@Override
	public Collection<SocketChannel> getChannels() {
		return channels;
	}

}
