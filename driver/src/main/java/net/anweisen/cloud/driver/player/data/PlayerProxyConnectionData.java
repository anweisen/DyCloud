package net.anweisen.cloud.driver.player.data;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerProxyConnectionData implements SerializableObject, UnspecifiedPlayerConnectionData {

	private UUID uniqueId;
	private String name;

	private HostAndPort address;

	private int version;

	private boolean onlineMode, legacy;

	private PlayerProxyConnectionData() {
	}

	public PlayerProxyConnectionData(@Nonnull UUID uniqueId, @Nonnull String name, @Nonnull HostAndPort address, int version, boolean onlineMode, boolean legacy) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.address = address;
		this.version = version;
		this.onlineMode = onlineMode;
		this.legacy = legacy;
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

	@Nonnull
	@Override
	public HostAndPort getAddress() {
		return address;
	}

	public int getVersion() {
		return version;
	}

	public boolean isLegacy() {
		return legacy;
	}

	public boolean isOnlineMode() {
		return onlineMode;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(name);
		buffer.writeObject(address);
		buffer.writeInt(version);
		buffer.writeBoolean(onlineMode);
		buffer.writeBoolean(legacy);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		name = buffer.readString();
		address = buffer.readObject(HostAndPort.class);
		version = buffer.readInt();
		onlineMode = buffer.readBoolean();
		legacy = buffer.readBoolean();
	}

	@Override
	public String toString() {
		return "PlayerProxyConnectionData[" + name + ":" + uniqueId + " address=" + address + " version=" + version + " legacy=" + legacy + " online=" + onlineMode + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayerProxyConnectionData that = (PlayerProxyConnectionData) o;
		return version == that.version
			&& onlineMode == that.onlineMode
			&& legacy == that.legacy
			&& Objects.equals(uniqueId, that.uniqueId)
			&& Objects.equals(name, that.name)
			&& Objects.equals(address, that.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, name, address, version, onlineMode, legacy);
	}
}
