package net.anweisen.cloud.driver.network.http;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpResponse extends HttpMessage<HttpResponse> {

	int getStatusCode();

	@Nonnull
	HttpResponse setStatusCode(int code);

}
