package net.anweisen.cloud.driver.player;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.player.data.PlayerNetworkProxyConnection;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudOfflinePlayer implements CloudOfflinePlayer {

	private UUID uuid;
	private String name;
	private PlayerNetworkProxyConnection lastNetworkConnection;
	private long firstLogin;
	private long lastOnline;
	private Document properties;

	public DefaultCloudOfflinePlayer(@Nonnull UUID uuid, @Nonnull String name, @Nonnull PlayerNetworkProxyConnection lastNetworkConnection, long firstLogin, long lastOnline, @Nonnull Document properties) {
		this.uuid = uuid;
		this.name = name;
		this.lastNetworkConnection = lastNetworkConnection;
		this.firstLogin = firstLogin;
		this.lastOnline = lastOnline;
		this.properties = properties;
	}

	@Nonnull
	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(@Nonnull String name) {
		Preconditions.checkNotNull(name, "Cannot set the name to null");
		this.name = name;
	}

	@Nullable
	@Override
	public PlayerNetworkProxyConnection getLastNetworkConnection() {
		return lastNetworkConnection;
	}

	@Override
	public long getFirstLoginTime() {
		return firstLogin;
	}

	@Override
	public long getLastOnlineTime() {
		return lastOnline;
	}

	@Override
	public void setLastOnlineTime(long lastOnlineTime) {
		this.lastOnline = lastOnlineTime;
	}

	@Nonnull
	@Override
	public Document getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "CloudOfflinePlayer[name=" + name + " uuid=" + uuid + "]";
	}
}
