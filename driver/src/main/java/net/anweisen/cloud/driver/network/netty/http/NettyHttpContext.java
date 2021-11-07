package net.anweisen.cloud.driver.network.netty.http;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import net.anweisen.cloud.driver.network.http.*;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpContext implements HttpContext {

	protected final io.netty.handler.codec.http.HttpRequest nettyRequest;
	protected final io.netty.channel.Channel nettyChannel;

	protected final URI uri;

	protected final NettyHttpChannel channel;
	protected final NettyHttpServer server;

	protected final NettyHttpRequest request;
	protected final NettyHttpResponse response;

	protected volatile NettyWebSocketChannel webSocketChannel;

	protected volatile boolean closeAfter = true;
	protected volatile boolean cancelNext = false;
	protected volatile boolean cancelSendResponse = false;

	public NettyHttpContext(@Nonnull URI uri, @Nonnull NettyHttpChannel channel, @Nonnull NettyHttpServer server,
	                        @Nonnull io.netty.handler.codec.http.HttpRequest nettyRequest, io.netty.channel.Channel nettyChannel,
	                        @Nonnull Map<String, String> pathParameters) {
		this.nettyRequest = nettyRequest;
		this.nettyChannel = nettyChannel;
		this.uri = uri;
		this.channel = channel;
		this.server = server;

		this.request = new NettyHttpRequest(this, nettyRequest, pathParameters);
		this.response = new NettyHttpResponse(this, nettyRequest);
	}

	@Nonnull
	@Override
	public WebSocketChannel upgrade() {
		if (webSocketChannel == null) {
			cancelSendResponse = true;

			WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory(
				nettyRequest.uri(),
				null,
				false
			);

			nettyChannel.pipeline().remove("http-server-handler");

			WebSocketServerHandshaker webSocketServerHandshaker = webSocketServerHandshakerFactory.newHandshaker(nettyRequest);
			webSocketServerHandshaker.handshake(nettyChannel, nettyRequest);

			webSocketChannel = new NettyWebSocketChannel(this, nettyChannel);
			nettyChannel.pipeline().addLast("websocket-server-handler", new NettyWebSocketChannelHandler(webSocketChannel));

			closeAfter = false;
		}

		return webSocketChannel;
	}

	@Nullable
	@Override
	public WebSocketChannel getWebSocketChannel() {
		return webSocketChannel;
	}

	@Override
	public boolean isCancelNext() {
		return cancelNext;
	}

	@Nonnull
	@Override
	public HttpContext cancelNext(boolean cancel) {
		this.cancelNext = cancel;
		return this;
	}

	@Override
	public boolean isCloseAfter() {
		return closeAfter;
	}

	@Nonnull
	@Override
	public HttpContext closeAfter(boolean close) {
		this.closeAfter = close;
		return this;
	}

	@Override
	public boolean isCancelSendResponse() {
		return cancelSendResponse;
	}

	@Nonnull
	@Override
	public HttpContext cancelSendResponse(boolean cancel) {
		this.cancelSendResponse = cancel;
		return this;
	}

	@Nonnull
	@Override
	public HttpChannel getChannel() {
		return channel;
	}

	@Nonnull
	@Override
	public HttpServer getServer() {
		return server;
	}

	@Nonnull
	@Override
	public HttpRequest getRequest() {
		return request;
	}

	@Nonnull
	@Override
	public HttpResponse getResponse() {
		return response;
	}
}
