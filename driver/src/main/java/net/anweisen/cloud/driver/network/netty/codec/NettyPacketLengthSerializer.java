package net.anweisen.cloud.driver.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.anweisen.cloud.driver.network.netty.NettyUtils;

import javax.annotation.Nonnull;

public final class NettyPacketLengthSerializer extends MessageToByteEncoder<ByteBuf> {

	@Override
	protected void encode(@Nonnull ChannelHandlerContext context, @Nonnull ByteBuf in, @Nonnull ByteBuf out) {
		NettyUtils.writeVarInt(out, in.readableBytes());
		out.writeBytes(in);
	}

	@Override
	protected ByteBuf allocateBuffer(@Nonnull ChannelHandlerContext context, @Nonnull ByteBuf message, boolean preferDirect) {
		int initialSize = 5 + message.readableBytes();
		return preferDirect
			? context.alloc().ioBuffer(initialSize)
			: context.alloc().heapBuffer(initialSize);
	}
}