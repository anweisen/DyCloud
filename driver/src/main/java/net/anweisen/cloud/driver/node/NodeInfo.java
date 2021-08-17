package net.anweisen.cloud.driver.node;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class NodeInfo implements SerializableObject {

	private String name;
	private HostAndPort address;

	private NodeInfo() {
	}

	public NodeInfo(@Nonnull String name, @Nonnull HostAndPort address) {
		this.name = name;
		this.address = address;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(name);
		buffer.writeObject(address);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		name = buffer.readString();
		address = buffer.readObject(HostAndPort.class);
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public HostAndPort getAddress() {
		return address;
	}
}
