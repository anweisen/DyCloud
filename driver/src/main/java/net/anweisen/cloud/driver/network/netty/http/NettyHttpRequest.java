package net.anweisen.cloud.driver.network.netty.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;
import net.anweisen.cloud.driver.network.http.HttpRequest;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpRequest implements HttpRequest, NettyDefaultHttpMessage<HttpRequest> {

	protected final io.netty.handler.codec.http.HttpRequest nettyRequest;
	protected final NettyHttpContext context;

	protected final Map<String, String> pathParameters;
	protected final Map<String, List<String>> queryParameters;

	protected byte[] body;

	public NettyHttpRequest(@Nonnull NettyHttpContext context, @Nonnull io.netty.handler.codec.http.HttpRequest nettyRequest, @Nonnull Map<String, String> pathParameters) {
		this.nettyRequest = nettyRequest;
		this.context = context;

		this.pathParameters = pathParameters;
		this.queryParameters = new QueryStringDecoder(nettyRequest.uri()).parameters();
	}

	@Nonnull
	@Override
	public HttpHeaders getNettyHeaders() {
		return nettyRequest.headers();
	}

	@Nonnull
	@Override
	public io.netty.handler.codec.http.HttpVersion getNettyHttpVersion() {
		return nettyRequest.protocolVersion();
	}

	@Override
	public void setNettyHttpVersion(@Nonnull HttpVersion version) {
		nettyRequest.setProtocolVersion(version);
	}

	@Nonnull
	@Override
	public HttpContext getContext() {
		return context;
	}

	@Nonnull
	@Override
	public Map<String, List<String>> getQueryParameters() {
		return queryParameters;
	}

	@Nonnull
	@Override
	public Map<String, String> getPathParameters() {
		return pathParameters;
	}

	@Nonnull
	@Override
	public byte[] getBody() {
		if (body != null) return body;

		if (nettyRequest instanceof FullHttpRequest) {
			FullHttpRequest fullRequest = (FullHttpRequest) nettyRequest;

			if (fullRequest.content().hasArray()) {
				body = fullRequest.content().array();
			} else {
				body = new byte[fullRequest.content().readableBytes()];
				fullRequest.content().getBytes(fullRequest.content().readerIndex(), body);
			}
		}

		return new byte[0];
	}

	@Nonnull
	@Override
	public HttpRequest setBody(@Nonnull byte[] data) {
		throw new UnsupportedOperationException("You cannot set the body of a client http request");
	}

	@Nonnull
	@Override
	public HttpMethod getMethod() {
		return HttpMethod.valueOf(nettyRequest.method().name());
	}

	@Nonnull
	@Override
	public URI getUri() {
		return context.uri;
	}

	@Nonnull
	@Override
	public String getPath() {
		return context.uri.getPath();
	}
}
