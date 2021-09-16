package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SocketClient extends SocketComponent {

	void connect(@Nonnull HostAndPort address);

}
