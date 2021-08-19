package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.data.PlayerNetworkProxyConnection;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudPlayer implements CloudPlayer, SerializableObject {

	private DefaultCloudOfflinePlayer offlinePlayer;
	private PlayerNetworkProxyConnection connection;
	private ServiceInfo server;
	private boolean online = true;

	public DefaultCloudPlayer(@Nonnull CloudOfflinePlayer offlinePlayer, @Nonnull PlayerNetworkProxyConnection connection) {
		this.offlinePlayer = (DefaultCloudOfflinePlayer) offlinePlayer;
		this.connection = connection;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeObject(offlinePlayer);
		buffer.writeObject(connection);
		buffer.writeBoolean(online);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		offlinePlayer = buffer.readObject(DefaultCloudOfflinePlayer.class);
		connection = buffer.readObject(PlayerNetworkProxyConnection.class);
		online = buffer.readBoolean();
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

	@Nonnull
	@Override
	public PermissionData getStoredPermissionData() {
		return offlinePlayer.getStoredPermissionData();
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

	@Nonnull
	@Override
	public PlayerNetworkProxyConnection getProxyConnection() {
		return connection;
	}

	@Nullable
	@Override
	public ServiceInfo getServer() {
		return server;
	}

	@Override
	public void setServer(@Nullable ServiceInfo server) {
		this.server = server;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public String toString() {
		return "CloudPlayer[name=" + getName() + " uuid=" + getUniqueId() + " address=" + getAddress() + " server=" + (server == null ? null : server.getName()) + "]";
	}
}
