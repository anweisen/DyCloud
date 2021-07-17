package net.anweisen.cloud.driver.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.anweisen.cloud.driver.network.netty.NettyUtils;

public final class NettyPacketLengthSerializer extends MessageToByteEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
		NettyUtils.writeVarInt(out, in.readableBytes());
		out.writeBytes(in);
	}

	@Override
	protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) {
		int initialSize = 5 + msg.readableBytes();
		return preferDirect
			? ctx.alloc().ioBuffer(initialSize)
			: ctx.alloc().heapBuffer(initialSize);
	}
}