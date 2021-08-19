package net.anweisen.cloud.driver.cord;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CordInfo implements SerializableObject {

	private String name;
	private HostAndPort address;
	private HostAndPort proxyAddress;

	private CordInfo() {
	}

	public CordInfo(@Nonnull String name, @Nonnull HostAndPort address, @Nonnull HostAndPort proxyAddress) {
		this.name = name;
		this.address = address;
		this.proxyAddress = proxyAddress;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(name);
		buffer.writeObject(address);
		buffer.writeObject(proxyAddress);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		name = buffer.readString();
		address = buffer.readObject(HostAndPort.class);
		proxyAddress = buffer.readObject(HostAndPort.class);
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public HostAndPort getClientAddress() {
		return address;
	}

	@Nonnull
	public HostAndPort getProxyAddress() {
		return proxyAddress;
	}

	@Override
	public String toString() {
		return "CordInfo[name=" + name + " clientAddress=" + address + " proxyAddress" + proxyAddress + "]";
	}
}
