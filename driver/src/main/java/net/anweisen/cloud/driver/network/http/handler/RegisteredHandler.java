package net.anweisen.cloud.driver.network.http.handler;

import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface RegisteredHandler {

	void execute(@Nonnull HttpContext context) throws Exception;

	/**
	 * The path of the handler.
	 * The path must start with '/' and must not end with '/'.
	 *
	 * @return the path of the handler
	 */
	@Nonnull
	String getPath();

	@Nonnull
	String getPermission();

	@Nonnull
	HttpMethod[] getMethods();

	@Nonnull
	Object getHolder();

}
