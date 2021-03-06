package net.anweisen.cloud.driver.player.defaults;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.connection.DefaultPlayerConnection;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.player.settings.DefaultPlayerSettings;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudPlayer implements CloudPlayer, SerializableObject {

	private DefaultCloudOfflinePlayer offlinePlayer;
	private DefaultPlayerConnection connection;
	private DefaultPlayerSettings settings;
	private UUID proxy;
	private UUID server;
	private long joinTime;
	private boolean online = true;
	private Document onlineProperties;

	private DefaultCloudPlayer() {
	}

	public DefaultCloudPlayer(@Nonnull CloudOfflinePlayer offlinePlayer, @Nonnull DefaultPlayerConnection connection, @Nonnull UUID proxy) {
		this.offlinePlayer = (DefaultCloudOfflinePlayer) offlinePlayer;
		this.connection = connection;
		this.proxy = proxy;
		this.joinTime = System.currentTimeMillis();
		this.onlineProperties = Documents.newJsonDocument();
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeObject(offlinePlayer);
		buffer.writeObject(connection);
		buffer.writeOptionalObject(settings);
		buffer.writeUniqueId(proxy);
		buffer.writeOptionalUniqueId(server);
		buffer.writeLong(joinTime);
		buffer.writeBoolean(online);
		buffer.writeDocument(onlineProperties);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		offlinePlayer = buffer.readObject(DefaultCloudOfflinePlayer.class);
		connection = buffer.readObject(DefaultPlayerConnection.class);
		settings = buffer.readOptionalObject(DefaultPlayerSettings.class);
		proxy = buffer.readUniqueId();
		server = buffer.readOptionalUniqueId();
		online = buffer.readBoolean();
		joinTime = buffer.readLong();
		onlineProperties = buffer.readDocument();
	}

	// BEGIN OF OFFLINE

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
	public String getRawLanguage() {
		return offlinePlayer.getRawLanguage();
	}

	@Nonnull
	@Override
	public String getLanguage() {
		return offlinePlayer.getLanguage();
	}

	@Override
	public void setLanguage(@Nonnull String language) {
		offlinePlayer.setLanguage(language);
	}

	@Nonnull
	@Override
	public PlayerConnection getLastConnection() {
		return offlinePlayer.getLastConnection();
	}

	@Override
	public void setLastConnection(@Nonnull PlayerConnection connection) {
		offlinePlayer.setLastConnection(connection);
	}

	@Override
	public long getFirstLoginTime() {
		return offlinePlayer.getFirstLoginTime();
	}

	@Override
	public void setFirstLoginTime(long time) {
		offlinePlayer.setFirstLoginTime(time);
	}

	@Override
	public long getLastOnlineTime() {
		return offlinePlayer.getLastOnlineTime();
	}

	@Override
	public void setLastOnlineTime(long time) {
		offlinePlayer.setLastOnlineTime(time);
	}

	@Nonnull
	@Override
	public PermissionData getStoredPermissionData() {
		return offlinePlayer.getStoredPermissionData();
	}

	@Nonnull
	@Override
	public Document getProperties() {
		return offlinePlayer.getProperties();
	}

	// END OF OFFLINE
	// BEGIN OF ONLINE

	@Nonnull
	@Override
	public PlayerConnection getConnection() {
		return connection;
	}

	@Nonnull
	@Override
	public PlayerSettings getSettings() {
		Preconditions.checkNotNull(settings, "No settings available (set in PlayerProxyLoginSuccessEvent)");
		return settings;
	}

	@Override
	public void setSettings(@Nonnull PlayerSettings settings) {
		this.settings = (DefaultPlayerSettings) settings;
	}

	@Nonnull
	@Override
	public ServiceInfo getProxy() {
		return Preconditions.checkNotNull(CloudDriver.getInstance().getServiceManager().getServiceInfoByUniqueId(proxy), "The proxy of the player no longer exists");
	}

	@Nullable
	@Override
	public ServiceInfo getServer() {
		return server == null ? null : CloudDriver.getInstance().getServiceManager().getServiceInfoByUniqueId(server);
	}

	@Nonnull
	@Override
	public Optional<ServiceInfo> getServerOptional() {
		return Optional.ofNullable(getServer());
	}

	@Override
	public void setCurrentServer(@Nullable UUID server) {
		this.server = server;
	}

	@Override
	public long getJoinTime() {
		return joinTime;
	}

	@Override
	public long getSessionDuration() {
		return System.currentTimeMillis() - joinTime;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void setOffline() {
		online = false;
	}

	@Override
	public void setOnlineDuration(long duration) {
		offlinePlayer.setOnlineDuration(duration);
	}

	@Override
	public long getOnlineDuration() {
		return offlinePlayer.getOnlineDuration() + getSessionDuration();
	}

	@Nonnull
	@Override
	public Document getOnlineProperties() {
		return onlineProperties;
	}

	@Override
	public void setOnlineProperties(@Nonnull Document properties) {
		this.onlineProperties = properties;
	}

	@Nonnull
	public DefaultCloudOfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}

	@Override
	public String toString() {
		return "CloudPlayer[name=" + getName() + " uuid=" + getUniqueId() + " address=" + connection.getAddress() + " version='" + connection.getVersion().getName() + "' proxy=" + getProxy().getName() + " server=" + (getServer() == null ? null : getServer().getName()) + (online ? "" : " online=false") + "]";
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
