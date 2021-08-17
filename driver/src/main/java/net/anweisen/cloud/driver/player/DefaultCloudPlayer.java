package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.player.data.PlayerNetworkProxyConnection;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudPlayer implements CloudPlayer {

	private final CloudOfflinePlayer offlinePlayer;
	private final PlayerNetworkProxyConnection connection;
	private boolean online = true;

	public DefaultCloudPlayer(@Nonnull CloudOfflinePlayer offlinePlayer, @Nonnull PlayerNetworkProxyConnection connection) {
		this.offlinePlayer = offlinePlayer;
		this.connection = connection;
	}

	@Nonnull
	@Override
	public UUID getUniqueId() {
		return offlinePlayer.getUniqueId();
	}

	@Nonnull
	@Override
	public String getName() {
		return offlinePlayer.getName();
	}

	@Override
	public void setName(@Nonnull String name) {
		offlinePlayer.setName(name);
	}

	@Nullable
	@Override
	public PlayerNetworkProxyConnection getLastNetworkConnection() {
		return offlinePlayer.getLastNetworkConnection();
	}

	@Override
	public long getFirstLoginTime() {
		return offlinePlayer.getFirstLoginTime();
	}

	@Override
	public long getLastOnlineTime() {
		return offlinePlayer.getLastOnlineTime();
	}

	@Override
	public void setLastOnlineTime(long lastOnlineTime) {
		offlinePlayer.setLastOnlineTime(lastOnlineTime);
	}

	@Nonnull
	@Override
	public Document getProperties() {
		return offlinePlayer.getProperties();
	}

	@Nonnull
	@Override
	public PlayerExecutor getExecutor() {
		return null;
	}

	@Nonnull
	@Override
	public HostAndPort getAddress() {
		return connection.getAddress();
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void setOnline(boolean online) {
		this.online = online;
	}

	@Nonnull
	public PlayerNetworkProxyConnection getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		return "CloudOnlinePlayer[name=" + getName() + " uuid=" + getUniqueId() + "]";
	}
}
