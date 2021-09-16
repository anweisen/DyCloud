package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SocketServer extends SocketComponent {

	void addListener(@Nonnull HostAndPort address);

	default void addListener(int port) {
		addListener(HostAndPort.localhost(port));
	}

	default void addListener(@Nonnull String host, int port) {
		addListener(new HostAndPort(host, port));
	}

}
