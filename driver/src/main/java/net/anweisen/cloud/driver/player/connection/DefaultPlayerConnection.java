package net.anweisen.cloud.driver.player.connection;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultPlayerConnection implements PlayerConnection, SerializableObject {

	private String proxy;
	private HostAndPort address;
	private int version;
	private boolean onlineMode, legacy;

	private DefaultPlayerConnection() {
	}

	public DefaultPlayerConnection(@Nonnull String proxy, @Nonnull HostAndPort address, int version, boolean onlineMode, boolean legacy) {
		this.proxy = proxy;
		this.address = address;
		this.version = version;
		this.onlineMode = onlineMode;
		this.legacy = legacy;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(proxy);
		buffer.writeObject(address);
		buffer.writeInt(version);
		buffer.writeBoolean(onlineMode);
		buffer.writeBoolean(legacy);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		proxy = buffer.readString();
		address = buffer.readObject(HostAndPort.class);
		version = buffer.readInt();
		onlineMode = buffer.readBoolean();
		legacy = buffer.readBoolean();
	}

	@Nonnull
	@Override
	public String getProxyName() {
		return proxy;
	}

	@Nonnull
	@Override
	public HostAndPort getAddress() {
		return address;
	}

	@Nonnull
	@Override
	public ProtocolVersion getVersion() {
		return ProtocolVersion.getVersion(version);
	}

	@Override
	public int getRawVersion() {
		return version;
	}

	@Override
	public boolean getOnlineMode() {
		return onlineMode;
	}

	@Override
	public boolean getLegacy() {
		return legacy;
	}
}
