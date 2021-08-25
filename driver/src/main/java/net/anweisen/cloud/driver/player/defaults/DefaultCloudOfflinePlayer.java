package net.anweisen.cloud.driver.player.defaults;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.data.PlayerNetworkProxyConnection;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudOfflinePlayer implements CloudOfflinePlayer, SerializableObject {

	private UUID uuid;
	private String name;
	private PlayerNetworkProxyConnection lastNetworkConnection;
	private PermissionData permissionData;
	private long firstLogin;
	private long lastOnline;
	private Document properties;

	public DefaultCloudOfflinePlayer(@Nonnull UUID uuid, @Nonnull String name, @Nonnull PlayerNetworkProxyConnection lastNetworkConnection,
	                                 @Nonnull PermissionData permissionData, long firstLogin, long lastOnline, @Nonnull Document properties) {
		this.uuid = uuid;
		this.name = name;
		this.lastNetworkConnection = lastNetworkConnection;
		this.permissionData = permissionData;
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

	@Nonnull
	@Override
	public PermissionData getStoredPermissionData() {
		return permissionData;
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
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uuid);
		buffer.writeString(name);
		buffer.writeObject(lastNetworkConnection);
		buffer.writeObject(permissionData);
		buffer.writeLong(firstLogin);
		buffer.writeLong(lastOnline);
		buffer.writeDocument(properties);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uuid = buffer.readUUID();
		name = buffer.readString();
		lastNetworkConnection = buffer.readObject(PlayerNetworkProxyConnection.class);
		permissionData = buffer.readObject(PermissionData.class);
		firstLogin = buffer.readLong();
		lastOnline = buffer.readLong();
		properties = buffer.readDocument();
	}

	@Override
	public String toString() {
		return "CloudOfflinePlayer[name=" + name + " uuid=" + uuid + "]";
	}
}
