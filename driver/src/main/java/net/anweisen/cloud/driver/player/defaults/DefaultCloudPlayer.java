package net.anweisen.cloud.driver.player.defaults;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.data.PlayerProxyConnectionData;
import net.anweisen.cloud.driver.player.data.PlayerServerConnectionData;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudPlayer implements CloudPlayer, SerializableObject {

	private DefaultCloudOfflinePlayer offlinePlayer;
	private PlayerProxyConnectionData proxyConnection;
	private ServiceInfo proxy;
	private PlayerServerConnectionData serverConnection;
	private ServiceInfo server;
	private boolean online = true;

	private DefaultCloudPlayer() {
	}

	public DefaultCloudPlayer(@Nonnull CloudOfflinePlayer offlinePlayer, @Nonnull PlayerProxyConnectionData connection, @Nonnull ServiceInfo proxy) {
		this.offlinePlayer = (DefaultCloudOfflinePlayer) offlinePlayer;
		this.proxyConnection = connection;
		this.proxy = proxy;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeObject(offlinePlayer);
		buffer.writeObject(proxyConnection);
		buffer.writeObject(proxy);
		buffer.writeOptionalObject(serverConnection);
		buffer.writeOptionalObject(server);
		buffer.writeBoolean(online);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		offlinePlayer = buffer.readObject(DefaultCloudOfflinePlayer.class);
		proxyConnection = buffer.readObject(PlayerProxyConnectionData.class);
		proxy = buffer.readObject(ServiceInfo.class);
		serverConnection = buffer.readOptionalObject(PlayerServerConnectionData.class);
		server = buffer.readOptionalObject(ServiceInfo.class);
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

	@Nonnull
	@Override
	public PlayerProxyConnectionData getLastProxyConnectionData() {
		return offlinePlayer.getLastProxyConnectionData();
	}

	@Override
	public void setLastProxyConnectionData(@Nonnull PlayerProxyConnectionData connectionData) {
		offlinePlayer.setLastProxyConnectionData(connectionData);
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
	public HostAndPort getAddress() {
		return proxyConnection.getAddress();
	}

	@Nonnull
	@Override
	public PlayerProxyConnectionData getProxyConnectionData() {
		return proxyConnection;
	}

	@Nonnull
	@Override
	public ServiceInfo getCurrentProxy() {
		return proxy;
	}

	@Nullable
	@Override
	public PlayerServerConnectionData getServerConnectionData() {
		return serverConnection;
	}

	@Override
	public void setServerConnectionData(@Nonnull PlayerServerConnectionData serverConnection) {
		this.serverConnection = serverConnection;
	}

	@Nullable
	@Override
	public ServiceInfo getCurrentServer() {
		return server;
	}

	@Override
	public void setCurrentServer(@Nonnull ServiceInfo server) {
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

	@Nonnull
	public DefaultCloudOfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}

	@Override
	public String toString() {
		return "CloudPlayer[name=" + getName() + " uuid=" + getUniqueId() + " address=" + getAddress() + " proxy=" + proxyConnection.getName() + " server=" + (server == null ? null : server.getName()) + (online ? "" : " online=false") + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultCloudPlayer that = (DefaultCloudPlayer) o;
		return Objects.equals(this.getName(), that.getName())
			&& Objects.equals(this.getUniqueId(), that.getUniqueId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getUniqueId());
	}
}
