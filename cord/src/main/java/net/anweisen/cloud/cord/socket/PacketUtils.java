package net.anweisen.cloud.cord.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;
import net.anweisen.cloud.cord.socket.stream.ProxyDownstreamHandler;
import net.anweisen.cloud.cord.socket.stream.ProxyUpstreamHandler;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PacketUtils {

	public static final AttributeKey<ConnectionState> CONNECTION_STATE = AttributeKey.valueOf("connectionstate");
	public static final AttributeKey<ProxyUpstreamHandler> UPSTREAM_HANDLER = AttributeKey.valueOf("upstreamhandler");
	public static final AttributeKey<ProxyDownstreamHandler> DOWNSTREAM_HANDLER = AttributeKey.valueOf("downstreamhandler");
	public static final AttributeKey<Integer> PROTOCOL_VERSION = AttributeKey.valueOf("protocolversion");

	@Nonnull
	public static ByteBuf createStatusPacket(int protocolVersion) {
		ByteBuf buffer = Unpooled.buffer();
		NettyUtils.writeVarInt(buffer, 0);
		NettyUtils.writeString(buffer, Document.create()
			.set("version", Document.create()
				.set("name", "DyCloud v1.0")
				.set("protocol", protocolVersion)
			).set("players", Document.create()
				.set("max", 1)
				.set("online", 0)
			).set("description", Document.create()
				.set("text", "§8» §7github.com/anweisen/§e§lDyCloud")
			).toJson());
		return buffer;
	}

	@Nonnull
	public static ByteBuf createKickPacket(@Nonnull String text) {
		ByteBuf buffer = Unpooled.buffer();
		NettyUtils.writeVarInt(buffer, 2 + text.length());
		NettyUtils.writeVarInt(buffer, 0);
		NettyUtils.writeString(buffer, text);
		return buffer;
	}

	private PacketUtils() {}

}
