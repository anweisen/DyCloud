package net.anweisen.cloud.driver.network.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketDecoder;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketEncoder;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketLengthDeserializer;
import net.anweisen.cloud.driver.network.netty.codec.NettyPacketLengthSerializer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyClientInitializer extends ChannelInitializer<Channel> {

	protected final NettySocketClient client;
	protected final HostAndPort address;

	public NettyClientInitializer(@Nonnull NettySocketClient client, @Nonnull HostAndPort address) {
		this.client = client;
		this.address = address;
	}

	@Override
	protected void initChannel(@Nonnull Channel channel) {
		channel.pipeline()
			.addLast("packet-length-deserializer", new NettyPacketLengthDeserializer())
			.addLast("packet-decoder", new NettyPacketDecoder())
			.addLast("packet-length-serializer", new NettyPacketLengthSerializer())
			.addLast("packet-encoder", new NettyPacketEncoder())
			.addLast("channel-handler", new NettyClientChannelHandler(client, address))
		;
	}

}
