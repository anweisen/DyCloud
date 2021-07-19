package net.anweisen.cloud.driver.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.logging.LogLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public final class NettyPacketDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(@Nullable ChannelHandlerContext context, @Nonnull ByteBuf buffer, @Nonnull List<Object> out) {
		if (context != null && (!context.channel().isActive() || !buffer.isReadable())) {
			buffer.clear();
			return;
		}

		try {
			int channel = NettyUtils.readVarInt(buffer);
			UUID uniqueId = new UUID(buffer.readLong(), buffer.readLong());
			Document header = this.readHeader(buffer);
			Buffer body = Buffer.wrap(NettyUtils.readByteArray(buffer, NettyUtils.readVarInt(buffer)));

			Packet packet = new Packet(channel, uniqueId, header, body);
			out.add(packet);

			this.showDebug(packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void showDebug(@Nonnull Packet packet) {
		if (packet.isShowDebug() && CloudDriver.getInstance() != null && CloudDriver.getInstance().getLogger().isLevelEnabled(LogLevel.DEBUG)) {
			CloudDriver.getInstance().getLogger().trace(
					"Successfully decoded packet on channel {} with id {}, header={};body={}",
					packet.getChannel(),
					packet.getUniqueId(),
					packet.getHeader().toJson(),
					packet.getBuffer() != null ? packet.getBuffer().readableBytes() : 0
			);
		}
	}

	@Nonnull
	protected Document readHeader(@Nonnull ByteBuf buffer) {
		int length = NettyUtils.readVarInt(buffer);
		if (length == 0) {
			return Document.empty();
		} else {
			byte[] content = new byte[length];
			buffer.readBytes(content);
			return Document.parseJson(new String(content, StandardCharsets.UTF_8));
		}
	}
}