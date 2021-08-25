package net.anweisen.cloud.driver.player.data;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerServerConnectionData implements SerializableObject, UnspecifiedPlayerConnectionData {

	private UUID uniqueId;
	private String name;

	private HostAndPort address;

	// TODO some ingame data?

	private PlayerServerConnectionData() {
	}

	public PlayerServerConnectionData(@Nonnull UUID uniqueId, @Nonnull String name, @Nullable HostAndPort address) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.address = address;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(name);
		buffer.writeOptionalObject(address);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		name = buffer.readString();
		address = buffer.readOptionalObject(HostAndPort.class);
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nonnull
	@Override
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Nullable
	@Override
	public HostAndPort getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "PlayerServerConnectionData[" + name + ":" + uniqueId + " address=" + address + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayerServerConnectionData that = (PlayerServerConnectionData) o;
		return Objects.equals(uniqueId, that.uniqueId)
			&& Objects.equals(name, that.name)
			&& Objects.equals(address, that.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, name, address);
	}
}
