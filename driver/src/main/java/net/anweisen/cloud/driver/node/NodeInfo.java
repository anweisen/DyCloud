package net.anweisen.cloud.driver.node;

import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.driver.network.object.IpRange;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class NodeInfo implements SerializableObject {

	private String name;
	private HostAndPort address;

	private String gateway;
	private IpRange subnet;

	private NodeInfo() {
	}

	public NodeInfo(@Nonnull String name, @Nonnull HostAndPort address, @Nonnull String gateway, @Nonnull String subnet) {
		this.name = name;
		this.address = address;
		this.gateway = gateway;
		this.subnet = new IpRange(subnet);
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeString(name);
		buffer.writeObject(address);
		buffer.writeString(gateway);
		buffer.writeInetAddress(subnet.getRequiredAddress());
		buffer.writeInt(subnet.getMaskBits());
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		name = buffer.readString();
		address = buffer.readObject(HostAndPort.class);
		gateway = buffer.readString();
		subnet = new IpRange(buffer.readInetAddress(), buffer.readInt());
	}

	/**
	 * @return the subnet of the docker network used
	 */
	@Nonnull
	public IpRange getSubnet() {
		return subnet;
	}

	/**
	 * @return the gateway of the docker network used
	 */
	@Nonnull
	public String getGateway() {
		return gateway;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public HostAndPort getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "NodeInfo[name=" + name + " address=" + address + "]";
	}
}
