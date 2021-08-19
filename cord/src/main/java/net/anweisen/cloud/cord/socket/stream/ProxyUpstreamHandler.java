package net.anweisen.cloud.cord.socket.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyUpstreamHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private final ProxyDownstreamHandler downstreamHandler;
	private Channel channel;

	public ProxyUpstreamHandler(@Nonnull Channel channel, @Nonnull ProxyDownstreamHandler downstreamHandler) {
		this.channel = channel;
		this.downstreamHandler = downstreamHandler;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, ByteBuf buffer) throws Exception {
		channel.writeAndFlush(buffer.retain());
	}

	@Override
	public void channelInactive(ChannelHandlerContext context) throws Exception {
		channel.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable ex) throws Exception {
	}

	public void setChannel(@Nonnull Channel channel) {
		this.channel = channel;
	}
}
