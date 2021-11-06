package net.anweisen.cloud.driver.network.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpServerInitializer extends ChannelInitializer<Channel> {

	protected final NettyHttpServer server;
	protected final HostAndPort address;

	public NettyHttpServerInitializer(@Nonnull NettyHttpServer server, @Nonnull HostAndPort address) {
		this.server = server;
		this.address = address;
	}

	@Override
	protected void initChannel(Channel channel) throws Exception {
		channel.pipeline()
			.addLast("http-server-codec", new HttpServerCodec())
			.addLast("http-object-aggregator", new HttpObjectAggregator(Short.MAX_VALUE))
			.addLast("http-server-handler", new NettyHttpChannelHandler(server, address))
		;
	}
}
