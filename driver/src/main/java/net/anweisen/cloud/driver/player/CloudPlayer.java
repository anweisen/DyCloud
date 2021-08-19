package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.player.data.PlayerNetworkProxyConnection;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

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

	@Nonnull
	PlayerNetworkProxyConnection getProxyConnection();

	@Nullable
	ServiceInfo getServer();

	void setServer(@Nullable ServiceInfo server);

	boolean isOnline();

	void setOnline(boolean online);

}
