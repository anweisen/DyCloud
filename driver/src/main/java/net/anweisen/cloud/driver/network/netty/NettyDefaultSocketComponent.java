package net.anweisen.cloud.driver.network.netty;

import io.netty.buffer.Unpooled;
import net.anweisen.cloud.driver.network.DefaultSocketComponent;
import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class NettyDefaultSocketComponent extends DefaultSocketComponent {

	public NettyDefaultSocketComponent(@Nonnull Supplier<SocketChannelHandler> handlerSupplier) {
		super(handlerSupplier);
	}

	@Nonnull
	@Override
	public PacketBuffer newPacketBuffer() {
		return new NettyPacketBuffer(Unpooled.buffer());
	}
}
