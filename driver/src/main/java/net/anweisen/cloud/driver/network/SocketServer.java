package net.anweisen.cloud.driver.network;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SocketServer extends SocketComponent {

	void addListener(@Nonnull HostAndPort address);

	default void addListener(int port) {
		addListener("0.0.0.0", port);
	}

	default void addListener(@Nonnull String host, int port) {
		addListener(new HostAndPort(host, port));
	}

}
