package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.node.NodeInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NodePublishPacket extends Packet {

	public NodePublishPacket(@Nonnull NodePublishPayload payload, @Nonnull NodeInfo info) {
		super(PacketConstants.NODE_INFO_PUBLISH_CHANNEL, Buffer.create().writeEnumConstant(payload).writeObject(info));
	}

	public enum NodePublishPayload {
		CONNECTED,
		DISCONNECTED
	}

}
