package net.anweisen.cloud.driver.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class NettyChannelHandler extends SimpleChannelInboundHandler<Packet> implements LoggingApiUser {

	protected NettyChannel channel;

	@Nonnull
	protected abstract Executor getPacketDispatcher();

	@Nonnull
	protected abstract Collection<SocketChannel> getChannels();

	@Nonnull
	protected abstract PacketListenerRegistry getListenerRegistry();

	@Override
	public void channelInactive(@Nonnull ChannelHandlerContext context) throws Exception {
		trace("Channel inactive: {}", channel);
		if (!context.channel().isActive() || !context.channel().isOpen() || !context.channel().isWritable()) {
			trace("=> Channel no longer active/open/writeable");

			if (channel.getHandler() != null)
				channel.getHandler().handleChannelClose(channel);

			context.channel().close();
			getChannels().remove(channel);
		}
	}

	@Override
	public void exceptionCaught(@Nonnull ChannelHandlerContext context, @Nonnull Throwable ex) throws Exception {
		trace("Error on channel {}: {}", channel, ex.getClass().getName(), ex.getMessage());
		if (!(ex instanceof IOException)) {
			ex.printStackTrace();
		}
	}

	@Override
	public void channelReadComplete(@Nonnull ChannelHandlerContext context) throws Exception {
		context.flush();
	}

	@Override
	protected void channelRead0(@Nonnull ChannelHandlerContext context, @Nonnull Packet packet) throws Exception {
		getPacketDispatcher().execute(() -> {
			try {

				if (channel.getHandler() == null || channel.getHandler().handlePacketReceive(channel, packet))
					getListenerRegistry().handlePacket(channel, packet);

			} catch (Exception ex) {
				CloudDriver.getInstance().getLogger().error("An error occurred while handling packet", ex);
			}
		});
	}

}
