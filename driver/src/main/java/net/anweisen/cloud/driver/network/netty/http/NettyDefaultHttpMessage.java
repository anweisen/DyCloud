package net.anweisen.cloud.driver.network.netty.http;

import io.netty.handler.codec.http.HttpHeaders;
import net.anweisen.cloud.driver.network.http.HttpMessage;
import net.anweisen.cloud.driver.network.http.HttpVersion;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface NettyDefaultHttpMessage<M extends HttpMessage<?>> extends HttpMessage<M> {

	@Nonnull
	HttpHeaders getNettyHeaders();

	@Nonnull
	io.netty.handler.codec.http.HttpVersion getNettyHttpVersion();

	void setNettyHttpVersion(@Nonnull io.netty.handler.codec.http.HttpVersion version);


	@Nullable
	@Override
	default String getHeader(@Nonnull String name) {
		return getNettyHeaders().getAsString(name);
	}

	@Override
	default int getHeaderInt(@Nonnull String name) {
		return getNettyHeaders().getInt(name);
	}

	@Override
	default boolean getHeaderBoolean(@Nonnull String name) {
		return Boolean.parseBoolean(getHeader(name));
	}

	@Override
	default boolean hasHeader(@Nonnull String name) {
		return getNettyHeaders().contains(name);
	}

	@Nonnull
	@Override
	default M setHeader(@Nonnull String name, @Nonnull String value) {
		getNettyHeaders().add(name, value);
		return self();
	}

	@Nonnull
	@Override
	default M removeHeader(@Nonnull String name) {
		getNettyHeaders().remove(name);
		return self();
	}

	@Nonnull
	@Override
	default M clearHeaders() {
		getNettyHeaders().clear();
		return self();
	}

	@Nonnull
	@Override
	default Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<>(getNettyHeaders().size());

		for (Entry<String, String> entry : getNettyHeaders().entries()) {
			headers.put(entry.getKey(), entry.getValue());
		}

		return headers;
	}

	@Nonnull
	@Override
	default HttpVersion getVersion() {
		if (getNettyHttpVersion() == io.netty.handler.codec.http.HttpVersion.HTTP_1_0) {
			return HttpVersion.HTTP_1_0;
		} else {
			return HttpVersion.HTTP_1_1;
		}
	}

	@Nonnull
	@Override
	default M setVersion(@Nonnull HttpVersion version) {
		if (version == HttpVersion.HTTP_1_0) {
			setNettyHttpVersion(io.netty.handler.codec.http.HttpVersion.HTTP_1_0);
		} else {
			setNettyHttpVersion(io.netty.handler.codec.http.HttpVersion.HTTP_1_1);
		}

		return self();
	}

	@Nonnull
	@Override
	default String getBodyString() {
		return new String(getBody(), StandardCharsets.UTF_8);
	}

	@Nonnull
	@Override
	default M setBody(@Nonnull String text) {
		return setBody(text.getBytes(StandardCharsets.UTF_8));
	}

	@Nonnull
	@Override
	default M setBody(@Nonnull Document document) {
		return setBody(document.toJson());
	}

	@Nonnull
	@Override
	default M setBody(@Nonnull Collection<Document> array) {
		return setBody(Document.toJson((Collection<?>) array));
	}

	@Nonnull
	@Override
	default M setBody(@Nonnull Document[] array) {
		return setBody(Arrays.asList(array));
	}

	@SuppressWarnings("unchecked")
	default M self() {
		return (M) this;
	}
}
