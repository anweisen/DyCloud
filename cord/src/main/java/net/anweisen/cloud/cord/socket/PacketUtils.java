package net.anweisen.cloud.cord.socket;

import io.netty.util.AttributeKey;
import net.anweisen.cloud.cord.socket.stream.ProxyDownstreamHandler;
import net.anweisen.cloud.cord.socket.stream.ProxyUpstreamHandler;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PacketUtils {

	public static final AttributeKey<ConnectionState> CONNECTION_STATE = AttributeKey.valueOf("connectionstate");
	public static final AttributeKey<ProxyUpstreamHandler> UPSTREAM_HANDLER = AttributeKey.valueOf("upstreamhandler");
	public static final AttributeKey<ProxyDownstreamHandler> DOWNSTREAM_HANDLER = AttributeKey.valueOf("downstreamhandler");

	private PacketUtils() {}

}
