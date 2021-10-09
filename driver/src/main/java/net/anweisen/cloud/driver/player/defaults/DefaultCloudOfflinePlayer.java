package net.anweisen.cloud.driver.player.defaults;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
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

	private UUID uniqueId;
	private String name;
	private String language;
	private DefaultPlayerConnection lastNetworkConnection;
	private PermissionData permissionData;
	private long firstLogin;
	private long lastOnline;
	private Document properties;

	private DefaultCloudOfflinePlayer() {
	}

	public DefaultCloudOfflinePlayer(@Nonnull UUID uniqueId, @Nonnull String name, @Nonnull String language, @Nonnull DefaultPlayerConnection lastNetworkConnection,
	                                 @Nonnull PermissionData permissionData, long firstLogin, long lastOnline, @Nonnull Document properties) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.language = language;
		this.lastNetworkConnection = lastNetworkConnection;
		this.permissionData = permissionData;
		this.firstLogin = firstLogin;
		this.lastOnline = lastOnline;
		this.properties = properties;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeUniqueId(uniqueId);
		buffer.writeString(name);
		buffer.writeObject(lastNetworkConnection);
		buffer.writeObject(permissionData);
		buffer.writeLong(firstLogin);
		buffer.writeLong(lastOnline);
		buffer.writeDocument(properties);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		uniqueId = buffer.readUniqueId();
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
		return uniqueId;
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
	public String getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(@Nonnull String language) {
		Preconditions.checkNotNull(language, "Cannot set language to null (empty string for unset)");
		this.language = language;
	}

	@Nonnull
	@Override
	public DefaultPlayerConnection getLastConnection() {
		return lastNetworkConnection;
	}

	public void setLastConnection(@Nonnull PlayerConnection connectionData) {
		Preconditions.checkNotNull(connectionData, "Cannot set the last connection to null");
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
	public void setFirstLoginTime(long time) {
		this.firstLogin = time;
	}

	@Override
	public long getLastOnlineTime() {
		return lastOnline;
	}

	@Override
	public void setLastOnlineTime(long time) {
		this.lastOnline = time;
	}

	@Nonnull
	@Override
	public Document getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "CloudOfflinePlayer[name=" + name + " uuid=" + uniqueId + "]";
	}
}
