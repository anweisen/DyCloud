package net.anweisen.cloud.driver.network.http;

import net.anweisen.cloud.driver.network.http.auth.HttpAuthRegistry;
import net.anweisen.cloud.driver.network.http.handler.HttpHandlerRegistry;
import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpServer {

	void addListener(@Nonnull HostAndPort address);

	void shutdown();

	@Nonnull
	HttpAuthRegistry getAuthRegistry();

	@Nonnull
	HttpHandlerRegistry getHandlerRegistry();

}
