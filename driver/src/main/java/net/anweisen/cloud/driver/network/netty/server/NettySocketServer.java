package net.anweisen.cloud.driver.network.netty.server;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.*;
import net.anweisen.cloud.driver.network.netty.NettyUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettySocketServer extends DefaultSocketComponent implements SocketServer {

	protected final EventLoopGroup bossEventLoopGroup = NettyUtils.newEventLoopGroup();
	protected final EventLoopGroup workerEventLoopGroup = NettyUtils.newEventLoopGroup();

	public NettySocketServer(@Nonnull Supplier<SocketChannelHandler> handler) {
		super(handler);
	}

	@Override
	public void addListener(@Nonnull HostAndPort address) {
		Preconditions.checkNotNull(address, "Address cannot be null");

		try {
			new ServerBootstrap()
				.group(this.bossEventLoopGroup, this.workerEventLoopGroup)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.IP_TOS, 24)
				.childOption(ChannelOption.AUTO_READ, true)
				.channelFactory(NettyUtils.getServerChannelFactory())
				.childHandler(new NettyServerInitializer(this, address))
				.bind(address.getHost(), address.getPort())
				.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
				.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
				.sync()
				.channel();

			CloudDriver.getInstance().getLogger().info("Socket listening on {}!", address);
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