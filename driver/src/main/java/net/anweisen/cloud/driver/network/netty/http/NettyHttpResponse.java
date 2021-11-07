package net.anweisen.cloud.driver.network.netty.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpResponse;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpResponse implements HttpResponse, NettyDefaultHttpMessage<HttpResponse> {

	protected final DefaultFullHttpResponse nettyResponse;
	protected final HttpContext context;

	public NettyHttpResponse(@Nonnull HttpContext context, @Nonnull HttpRequest nettyHttpRequest) {
		this.context = context;
		this.nettyResponse = new DefaultFullHttpResponse(
			nettyHttpRequest.protocolVersion(),
			HttpResponseStatus.NOT_FOUND,
			Unpooled.buffer()
		);
	}

	@Nonnull
	@Override
	public HttpContext getContext() {
		return context;
	}

	@Override
	public int getStatusCode() {
		return nettyResponse.status().code();
	}

	@Nonnull
	@Override
	public HttpResponse setStatusCode(int code) {
		nettyResponse.setStatus(HttpResponseStatus.valueOf(code));
		return this;
	}

	@Nonnull
	@Override
	public byte[] getBody() {
		return nettyResponse.content().array();
	}

	@Nonnull
	@Override
	public HttpResponse setBody(@Nonnull byte[] data) {
		nettyResponse.content().clear();
		nettyResponse.content().writeBytes(data);
		return this;
	}

	@Nonnull
	@Override
	public HttpHeaders getNettyHeaders() {
		return nettyResponse.headers();
	}

	@Nonnull
	@Override
	public HttpVersion getNettyHttpVersion() {
		return nettyResponse.protocolVersion();
	}

	@Override
	public void setNettyHttpVersion(@Nonnull HttpVersion version) {
		nettyResponse.setProtocolVersion(version);
	}
}
