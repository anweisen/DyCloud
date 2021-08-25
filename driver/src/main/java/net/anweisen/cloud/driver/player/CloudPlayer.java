package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.player.data.PlayerProxyConnectionData;
import net.anweisen.cloud.driver.player.data.PlayerServerConnectionData;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CloudPlayer extends CloudOfflinePlayer {

	@Nonnull
	default PlayerExecutor getExecutor() {
		return CloudDriver.getInstance().getPlayerManager().getPlayerExecutor(getUniqueId());
	}

	@Nonnull
	HostAndPort getAddress();

	@Nonnull
	PlayerProxyConnectionData getProxyConnectionData();

	@Nonnull
	ServiceInfo getCurrentProxy();

	@Nullable
	PlayerServerConnectionData getServerConnectionData();

	void setServerConnectionData(@Nonnull PlayerServerConnectionData serverConnection);

	@Nullable
	ServiceInfo getCurrentServer();

	void setCurrentServer(@Nonnull ServiceInfo server);

	boolean isOnline();

	void setOnline(boolean online);

}
