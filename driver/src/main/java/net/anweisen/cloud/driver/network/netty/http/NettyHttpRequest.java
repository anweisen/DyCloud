package net.anweisen.cloud.driver.network.netty.http;

import io.netty.handler.codec.http.QueryStringDecoder;
import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;
import net.anweisen.cloud.driver.network.http.HttpRequest;
import net.anweisen.cloud.driver.network.http.HttpVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpRequest implements HttpRequest {

	protected final io.netty.handler.codec.http.HttpRequest request;
	protected final HttpContext context;

	protected final Map<String, String> pathParameters;
	protected final Map<String, List<String>> queryParameters;

	public NettyHttpRequest(@Nonnull HttpContext context, @Nonnull io.netty.handler.codec.http.HttpRequest request, @Nonnull URI uri, @Nonnull Map<String, String> pathParameters) {
		this.request = request;
		this.context = context;

		this.pathParameters = pathParameters;
		this.queryParameters = new QueryStringDecoder(request.uri()).parameters();
	}

	@Nonnull
	@Override
	public HttpContext getContext() {
		return context;
	}

	@Nullable
	@Override
	public String getHeader(@Nonnull String name) {
		return request.headers().getAsString(name);
	}

	@Override
	public int getHeaderInt(@Nonnull String name) {
		return 0;
	}

	@Override
	public boolean getHeaderBoolean(@Nonnull String name) {
		return false;
	}

	@Override
	public boolean hasHeader(@Nonnull String name) {
		return false;
	}

	@Nonnull
	@Override
	public HttpRequest addHeader(@Nonnull String name, @Nonnull String value) {
		return null;
	}

	@Nonnull
	@Override
	public HttpRequest removeHeader(@Nonnull String name) {
		return null;
	}

	@Nonnull
	@Override
	public HttpRequest clearHeaders() {
		return null;
	}

	@Nonnull
	@Override
	public Map<String, String> getHeaders() {
		Map<String, String> maps = new HashMap<>(request.headers().size());

		for (Entry<String, String> entry : request.headers().entries()) {
			maps.put(entry.getKey(), entry.getValue());
		}

		return maps;
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
	public HttpVersion getVersion() {
		return null;
	}

	@Nonnull
	@Override
	public HttpRequest setVersion(@Nonnull HttpVersion version) {
		return null;
	}

	@Nonnull
	@Override
	public byte[] getBody() {
		return new byte[0];
	}

	@Nonnull
	@Override
	public String getBodyString() {
		return null;
	}

	@Nonnull
	@Override
	public HttpRequest setBody(@Nonnull byte[] data) {
		return null;
	}

	@Nonnull
	@Override
	public HttpRequest setBody(@Nonnull String text) {
		return null;
	}

	@Nonnull
	@Override
	public HttpMethod getMethod() {
		return HttpMethod.valueOf(request.method().name());
	}

	@Nonnull
	@Override
	public String getUri() {
		return null;
	}

	@Nonnull
	@Override
	public String getPath() {
		return null;
	}
}
