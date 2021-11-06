package net.anweisen.cloud.driver.network.netty.http;

import net.anweisen.cloud.driver.network.http.HttpChannel;
import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpRequest;
import net.anweisen.cloud.driver.network.http.HttpServer;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpContext implements HttpContext {

	protected final URI uri;

	protected final NettyHttpChannel channel;
	protected final NettyHttpServer server;

	protected final NettyHttpRequest request;

	protected volatile boolean closeAfter = true;
	protected volatile boolean cancelNext = false;

	public NettyHttpContext(@Nonnull URI uri, @Nonnull NettyHttpChannel channel, @Nonnull NettyHttpServer server, @Nonnull io.netty.handler.codec.http.HttpRequest request,
	                        @Nonnull Map<String, String> pathParameters) {
		this.uri = uri;
		this.channel = channel;
		this.server = server;

		this.request = new NettyHttpRequest(this, request, uri, pathParameters);
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
}
