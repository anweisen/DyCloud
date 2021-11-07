package net.anweisen.cloud.driver.network.http.websocket;

import net.anweisen.cloud.driver.network.http.HttpChannel;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface WebSocketChannel {

	@Nonnull
	HttpChannel getChannel();

	void close();

	void close(int statusCode, @Nonnull String closeReason);

	void sendFrame(@Nonnull WebSocketFrameType type, @Nonnull byte[] data);

	void sendFrame(@Nonnull WebSocketFrameType type, @Nonnull String text);

	@Nonnull
	Collection<WebSocketListener> getListeners();

	void addListener(@Nonnull WebSocketListener listener);

	default void addListeners(@Nonnull WebSocketListener... listeners) {
		for (WebSocketListener listener : listeners)
			addListener(listener);
	}

	void clearListeners();

}
