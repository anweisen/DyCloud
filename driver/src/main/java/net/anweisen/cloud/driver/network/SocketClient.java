package net.anweisen.cloud.driver.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SocketClient extends SocketComponent {

	void connect(@Nonnull HostAndPort address, @Nullable String localAddress);

	default void connect(@Nonnull HostAndPort address) {
		connect(address, null);
	}

}
