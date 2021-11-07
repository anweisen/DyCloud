package net.anweisen.cloud.driver.network.http.websocket;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface WebSocketListener {

	void handle(@Nonnull WebSocketChannel channel, @Nonnull WebSocketFrameType type, @Nonnull byte[] data); // TODO optimize this, we dont wanna handle bytes here

}
