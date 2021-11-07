package net.anweisen.cloud.driver.network.http;

import net.anweisen.cloud.driver.network.http.websocket.WebSocketChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpContext {

	@Nonnull
	WebSocketChannel upgrade();

	@Nullable
	WebSocketChannel getWebSocketChannel();

	@Nonnull
	HttpChannel getChannel();

	@Nonnull
	HttpServer getServer();

	@Nonnull
	HttpRequest getRequest();

	@Nonnull
	HttpResponse getResponse();

	@Nonnull
	HttpContext cancelNext(boolean cancel);

	boolean isCancelNext();

	@Nonnull
	HttpContext closeAfter(boolean close);

	boolean isCloseAfter();

	@Nonnull
	HttpContext cancelSendResponse(boolean cancel);

	boolean isCancelSendResponse();

}
