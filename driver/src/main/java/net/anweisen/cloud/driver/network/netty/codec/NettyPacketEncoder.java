package net.anweisen.cloud.driver.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.utilities.common.logging.LogLevel;

import javax.annotation.Nonnull;

public final class NettyPacketEncoder extends MessageToByteEncoder<Packet> {

	@Override
	protected void encode(@Nonnull ChannelHandlerContext context, @Nonnull Packet packet, @Nonnull ByteBuf buffer) {
		if (packet.isShowDebug() && CloudDriver.getInstance() != null && CloudDriver.getInstance().getLogger().isLevelEnabled(LogLevel.DEBUG)) {
			CloudDriver.getInstance().getLogger().trace(
				"Successfully encoded packet on channel {} with id {}, header={};body={}",
				packet.getChannel(),
				packet.getUniqueId(),
				packet.getHeader().toJson(),
				packet.getBuffer() != null ? packet.getBuffer().readableBytes() : 0
			);
		}

		// channel
		NettyUtils.writeVarInt(buffer, packet.getChannel());
		// unique id
		buffer
			.writeLong(packet.getUniqueId().getMostSignificantBits())
			.writeLong(packet.getUniqueId().getLeastSignificantBits());
		// header
		this.writeHeader(packet, buffer);
		// body
		if (packet.getBuffer() != null) {
			int amount = packet.getBuffer().readableBytes();
			NettyUtils.writeVarInt(buffer, amount);
			buffer.writeBytes(packet.getBuffer(), 0, amount);
		} else {
			NettyUtils.writeVarInt(buffer, 0);
		}
	}

	private void writeHeader(@Nonnull Packet packet, @Nonnull ByteBuf buffer) {
		if (packet.getHeader() == null || packet.getHeader().isEmpty()) {
			NettyUtils.writeVarInt(buffer, 0);
		} else {
			NettyUtils.writeString(buffer, packet.getHeader().toJson());
		}
	}
}