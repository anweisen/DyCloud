package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.NodeInfoPublishPacket.NodePublishType;
import net.anweisen.cloud.driver.node.NodeInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NodeInfoPublishListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {

		NodePublishType publishType = packet.getBuffer().readEnumConstant(NodePublishType.class);
		NodeInfo nodeInfo = packet.getBuffer().readObject(NodeInfo.class);

		CloudDriver.getInstance().getNodeManager().handleNodeUpdate(publishType, nodeInfo);

	}
}
