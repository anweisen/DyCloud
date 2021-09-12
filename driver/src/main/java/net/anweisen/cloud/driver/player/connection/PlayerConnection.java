package net.anweisen.cloud.driver.player.connection;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudPlayer#getConnection()
 */
public interface PlayerConnection {

	@Nonnull
	String getProxyName();

	@Nonnull
	HostAndPort getAddress();

	@Nonnull
	ProtocolVersion getVersion();

	int getRawVersion();

	boolean getOnlineMode();

	boolean getLegacy();

}
