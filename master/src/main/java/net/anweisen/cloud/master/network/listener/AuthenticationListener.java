package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ConfigInitPacket;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.request.RequestType;
import net.anweisen.cloud.driver.network.request.RequestUtils;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.network.packets.NetworkAuthResponsePacket;
import net.anweisen.cloud.master.node.DefaultNodeServer;
import net.anweisen.cloud.master.node.NodeServer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class AuthenticationListener implements PacketListener {

	private final CloudMaster cloud;

	public AuthenticationListener(@Nonnull CloudMaster cloud) {
		this.cloud = cloud;
	}

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {

		cloud.getLogger().trace("Received authentication from {}", channel);

		Buffer buffer = packet.getBuffer();

		String nodeName = buffer.readString();
		UUID identity = buffer.readUUID();

		if (!cloud.getConfig().getIdentity().equals(identity)) {
			cloud.getLogger().info("Authentication for node {} with identity {} was rejected: {}", nodeName, identity, channel);
			reject(channel, "authentication failed");
			return;
		}

		if (cloud.getNodeManager().getNodeInfos().stream().anyMatch(info -> info.getName().equalsIgnoreCase(nodeName))) {
			cloud.getLogger().info("{} tried to register with duplicate node name {}", channel, nodeName);
			reject(channel, "node already registered");
			return;
		}

		NodeInfo info = new NodeInfo(nodeName, channel.getClientAddress());
		NodeServer server = new DefaultNodeServer(info, channel);
		cloud.getNodeManager().getNodeServers().add(server);

		cloud.getLogger().info("Node '{}' has connected successfully", nodeName);
		channel.sendPacket(new NetworkAuthResponsePacket(true, "successful"));
		channel.sendPacket(ConfigInitPacket.create());

		// TODO
		Thread.sleep(3000);
		cloud.getServiceFactory().createService(new ArrayList<>(cloud.getServiceConfigManager().getTasks()).get(0));

	}

	private void reject(@Nonnull SocketChannel channel, @Nonnull String message) throws Exception {
		channel.sendPacket(new NetworkAuthResponsePacket(false, message));
		channel.close();
	}

}
