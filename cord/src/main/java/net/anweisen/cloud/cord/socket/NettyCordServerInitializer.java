package net.anweisen.cloud.cord.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Sharable
public class NettyCordServerInitializer extends ChannelInitializer<Channel> {

	private final NettyCordSocketServer server;

	public NettyCordServerInitializer(@Nonnull NettyCordSocketServer server) {
		this.server = server;
	}

	@Override
	protected void initChannel(@Nonnull Channel channel) throws Exception {
		channel.pipeline().addLast("minecraft-decoder", new NettyMinecraftDecoder(server));
	}

}
