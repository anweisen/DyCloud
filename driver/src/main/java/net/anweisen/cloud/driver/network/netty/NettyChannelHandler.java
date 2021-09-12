package net.anweisen.cloud.driver.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.def.InternalNetworkingPacket;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class NettyChannelHandler extends SimpleChannelInboundHandler<Packet> implements LoggingApiUser {

	protected NettyChannel channel;

	@Override
	public void channelInactive(@Nonnull ChannelHandlerContext context) throws Exception {
		trace("Channel inactive: {}", channel);
		if (!context.channel().isActive() || !context.channel().isOpen() || !context.channel().isWritable()) {
			trace("=> Channel no longer active/open/writeable");

			if (channel.getHandler() != null)
				channel.getHandler().handleChannelClose(channel);

			context.channel().close();
			getComponent().getChannels().remove(channel);
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
		getComponent().getPacketDispatcher().execute(() -> {
			try {

				if (packet.getChannel() == PacketConstants.INTERNAL_NETWORKING_CHANNEL) {
					InternalNetworkingPacket.handle(getComponent(), packet.getBuffer());
					return;
				}
				if (channel.getHandler() == null || channel.getHandler().handlePacketReceive(channel, packet))
					getComponent().getListenerRegistry().handlePacket(channel, packet);

			} catch (Exception ex) {
				CloudDriver.getInstance().getLogger().error("An error occurred while handling packet", ex);
			}
		});
	}

	@Nonnull
	public abstract SocketComponent getComponent();

}
