package net.anweisen.cloud.driver.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.netty.NettyPacketBuffer;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

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
			Document header = readHeader(buffer);
			PacketBuffer packetBuffer = new NettyPacketBuffer(Unpooled.wrappedBuffer(NettyUtils.readByteArray(buffer, NettyUtils.readVarInt(buffer))));

			Packet packet = new Packet(channel, uniqueId, header, packetBuffer);
			out.add(packet);

			if (CloudDriver.getInstance() != null )
				CloudDriver.getInstance().getLogger().trace("Successfully decoded {}", packet);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Nonnull
	private Document readHeader(@Nonnull ByteBuf buffer) {
		int length = NettyUtils.readVarInt(buffer);
		if (length == 0) {
			return Documents.emptyDocument();
		} else {
			byte[] content = new byte[length];
			buffer.readBytes(content);
			return Documents.newJsonDocument(new String(content, StandardCharsets.UTF_8));
		}
	}
}
