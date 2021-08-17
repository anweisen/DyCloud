package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.network.HostAndPort;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CloudPlayer extends CloudOfflinePlayer {

	@Nonnull
	PlayerExecutor getExecutor();

	@Nonnull
	HostAndPort getAddress();

	boolean isOnline();

	void setOnline(boolean online);

}
