package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.node.NodeInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NodePublishPacket extends Packet {

	public NodePublishPacket(@Nonnull NodePublishPayload payload, @Nonnull NodeInfo info) {
		super(PacketChannels.NODE_INFO_PUBLISH_CHANNEL, newBuffer().writeEnum(payload).writeObject(info));
	}

	public enum NodePublishPayload {
		CONNECTED,
		DISCONNECTED
	}

}
