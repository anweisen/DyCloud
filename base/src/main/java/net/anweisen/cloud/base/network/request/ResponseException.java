package net.anweisen.cloud.base.network.request;

import net.anweisen.cloud.driver.network.request.RequestResponseType;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ResponseException extends RuntimeException {

	private final RequestResponseType response;

	public ResponseException(@Nonnull RequestResponseType response) {
		this.response = response;
	}

	@Nonnull
	public RequestResponseType getResponse() {
		return response;
	}
}
