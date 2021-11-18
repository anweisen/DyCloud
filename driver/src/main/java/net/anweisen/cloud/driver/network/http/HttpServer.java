package net.anweisen.cloud.driver.network.http;

import net.anweisen.cloud.driver.network.http.auth.HttpAuthHandler;
import net.anweisen.cloud.driver.network.http.auth.HttpAuthRegistry;
import net.anweisen.cloud.driver.network.http.auth.HttpAuthUser;
import net.anweisen.cloud.driver.network.http.handler.HttpHandlerRegistry;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketChannel;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketFrameType;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.utilities.common.collection.pair.Tuple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpServer {

	void addListener(@Nonnull HostAndPort address);

	void shutdown();

	@Nonnull
	Collection<WebSocketChannel> getWebSocketChannels();

	void sendWebSocketFrame(@Nonnull WebSocketFrameType type, @Nonnull byte[] data);

	void sendWebSocketFrame(@Nonnull WebSocketFrameType type, @Nonnull String text);

	@Nonnull
	HttpAuthRegistry getAuthRegistry();

	@Nonnull
	HttpHandlerRegistry getHandlerRegistry();

	void applyUserAuth(@Nonnull Tuple<HttpAuthHandler, HttpAuthUser> values, @Nullable String header);

}
