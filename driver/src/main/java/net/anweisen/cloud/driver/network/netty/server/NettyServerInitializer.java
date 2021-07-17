package net.anweisen.cloud.driver.network.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketDecoder;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketEncoder;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketLengthDeserializer;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketLengthSerializer;

import javax.annotation.Nonnull;

public class NettyServerInitializer extends ChannelInitializer<Channel> {

	protected final NettySocketServer server;
	protected final HostAndPort address;

	public NettyServerInitializer(@Nonnull NettySocketServer server, @Nonnull HostAndPort address) {
		this.server = server;
		this.address = address;
	}

	@Override
	protected void initChannel(@Nonnull Channel channel) {
		channel.pipeline()
			.addLast("packet-length-deserializer", new NettyPacketLengthDeserializer())
			.addLast("packet-decoder", new NettyPacketDecoder())
			.addLast("packet-length-serializer", new NettyPacketLengthSerializer())
			.addLast("packet-encoder", new NettyPacketEncoder())
			.addLast("channel-handler", new NettyServerChannelHandler(this.server, this.address))
		;
	}
}