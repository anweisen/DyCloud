package net.anweisen.cloud.cord.socket.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.anweisen.cloud.cord.CloudCord;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * Cord <-> Proxy
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyDownstreamHandler extends SimpleChannelInboundHandler<ByteBuf> implements LoggingApiUser {

	private final Channel channel;
	private final HostAndPort clientAddress;

	public ProxyDownstreamHandler(@Nonnull Channel channel) {
		this.channel = channel;
		this.clientAddress = HostAndPort.fromSocketAddress(channel.remoteAddress());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, ByteBuf buffer) throws Exception {
//		trace("Forwarding downstream packet to client {}", clientAddress);
		CloudCord.getInstance().getTrafficReporter().reportDownstreamPacket(buffer.readableBytes());
		channel.writeAndFlush(buffer.retain());
	}

	@Override
	public void channelInactive(ChannelHandlerContext context) throws Exception {
		info("[{}] Downstream got disconnected", clientAddress);
		channel.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable ex) throws Exception {
	}

	@Nonnull
	public Channel getChannel() {
		return channel;
	}

	@Nonnull
	public HostAndPort getClientAddress() {
		return clientAddress;
	}
}
