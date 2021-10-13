package net.anweisen.cloud.cord.socket.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.anweisen.cloud.cord.CloudCord;
import net.anweisen.cloud.driver.console.LoggingApiUser;

import javax.annotation.Nonnull;

/**
 * Client <-> Cord
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyUpstreamHandler extends SimpleChannelInboundHandler<ByteBuf> implements LoggingApiUser {

	private final ProxyDownstreamHandler downstreamHandler;
	private Channel channel;

	public ProxyUpstreamHandler(@Nonnull Channel channel, @Nonnull ProxyDownstreamHandler downstreamHandler) {
		this.channel = channel;
		this.downstreamHandler = downstreamHandler;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, ByteBuf buffer) throws Exception {
//		trace("Forwarding upstream packet from {} to proxy", downstreamHandler.getClientAddress());
		CloudCord.getInstance().getTrafficReporter().reportUpstreamPacket(buffer.readableBytes());
		channel.writeAndFlush(buffer.retain());
	}

	@Override
	public void channelInactive(ChannelHandlerContext context) throws Exception {
		info("[{}] Upstream got disconnected", downstreamHandler.getClientAddress());
		channel.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable ex) throws Exception {
	}

	public void setChannel(@Nonnull Channel channel) {
		this.channel = channel;
	}

	@Nonnull
	public Channel getChannel() {
		return channel;
	}
}
