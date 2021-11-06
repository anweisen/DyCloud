package net.anweisen.cloud.driver.network.http;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum HttpVersion {

	HTTP_1_0("HTTP/1.0"),
	HTTP_1_1("HTTP/1.1");

	private final String value;

	HttpVersion(@Nonnull String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
