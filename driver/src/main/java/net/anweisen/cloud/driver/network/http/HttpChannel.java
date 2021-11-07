package net.anweisen.cloud.driver.network.http;

import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpChannel {

	@Nonnull
	HostAndPort getServerAddress();

	@Nonnull
	HostAndPort getClientAddress();

	void close();

}
