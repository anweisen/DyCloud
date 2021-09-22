package net.anweisen.cloud.driver.player.defaults;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.connection.DefaultPlayerConnection;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudOfflinePlayer implements CloudOfflinePlayer, SerializableObject {

	private UUID uuid;
	private String name;
	private DefaultPlayerConnection lastNetworkConnection;
	private PermissionData permissionData;
	private long firstLogin;
	private long lastOnline;
	private Document properties;

	private DefaultCloudOfflinePlayer() {
	}

	public DefaultCloudOfflinePlayer(@Nonnull UUID uuid, @Nonnull String name, @Nonnull DefaultPlayerConnection lastNetworkConnection,
	                                 @Nonnull PermissionData permissionData, long firstLogin, long lastOnline, @Nonnull Document properties) {
		this.uuid = uuid;
		this.name = name;
		this.lastNetworkConnection = lastNetworkConnection;
		this.permissionData = permissionData;
		this.firstLogin = firstLogin;
		this.lastOnline = lastOnline;
		this.properties = properties;
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
		lastNetworkConnection = buffer.readObject(DefaultPlayerConnection.class);
		permissionData = buffer.readObject(PermissionData.class);
		firstLogin = buffer.readLong();
		lastOnline = buffer.readLong();
		properties = buffer.readDocument();
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

	@Nonnull
	@Override
	public DefaultPlayerConnection getLastConnection() {
		return lastNetworkConnection;
	}

	public void setLastConnection(@Nonnull PlayerConnection connectionData) {
		this.lastNetworkConnection = (DefaultPlayerConnection) connectionData;
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
	public String toString() {
		return "CloudOfflinePlayer[name=" + name + " uuid=" + uuid + "]";
	}
}
