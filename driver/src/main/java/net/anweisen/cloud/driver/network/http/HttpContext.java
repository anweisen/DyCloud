package net.anweisen.cloud.driver.network.http;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpContext {

	@Nonnull
	HttpChannel getChannel();

	@Nonnull
	HttpServer getServer();

	@Nonnull
	HttpRequest getRequest();

	@Nonnull
	HttpContext cancelNext(boolean cancel);

	boolean isCancelNext();

	@Nonnull
	HttpContext closeAfter(boolean close);

	boolean isCloseAfter();

}
