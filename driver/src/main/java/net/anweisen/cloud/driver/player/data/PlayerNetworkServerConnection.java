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
public class PlayerNetworkServerConnection implements SerializableObject, UnspecifiedPlayerNetworkConnection {

	private UUID uniqueId;
	private String name;

	private HostAndPort address;

	private String serviceName;
	private UUID serviceUniqueId;

	// TODO some ingame data?

	private PlayerNetworkServerConnection() {
	}

	public PlayerNetworkServerConnection(@Nonnull UUID uniqueId, @Nonnull String name, @Nullable HostAndPort address, @Nonnull String serviceName, @Nonnull UUID serviceUniqueId) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.address = address;
		this.serviceName = serviceName;
		this.serviceUniqueId = serviceUniqueId;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(name);
		buffer.writeOptionalObject(address);
		buffer.writeString(serviceName);
		buffer.writeUUID(serviceUniqueId);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		name = buffer.readString();
		address = buffer.readOptionalObject(HostAndPort.class);
		serviceName = buffer.readString();
		serviceUniqueId = buffer.readUUID();
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

	@Nonnull
	public String getServiceName() {
		return serviceName;
	}

	@Nonnull
	public UUID getServiceUniqueId() {
		return serviceUniqueId;
	}

	@Override
	public String toString() {
		return "PlayerNetworkServerConnection[" + name + ":" + uniqueId + " service=" + serviceName + " address=" + address + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayerNetworkServerConnection that = (PlayerNetworkServerConnection) o;
		return Objects.equals(uniqueId, that.uniqueId)
			&& Objects.equals(name, that.name)
			&& Objects.equals(address, that.address)
			&& Objects.equals(serviceName, that.serviceName)
			&& Objects.equals(serviceUniqueId, that.serviceUniqueId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, name, address, serviceName, serviceUniqueId);
	}
}
