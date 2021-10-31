package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.base.node.NodeCycleData;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.node.NodeServer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NodeDataCycleListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		NodeCycleData data = packet.getBuffer().readObject(NodeCycleData.class);
		NodeServer node = CloudMaster.getInstance().getNodeManager().getNodeServer(channel);
		debug("{} -> {}", node, data);
		node.setLastCycleData(data);
	}

}
