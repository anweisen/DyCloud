package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket.AuthenticationType;
import net.anweisen.cloud.driver.network.packet.def.ConfigInitPacket;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.network.packets.NetworkAuthResponsePacket;
import net.anweisen.cloud.master.node.DefaultNodeServer;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
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

		Buffer buffer = packet.getBuffer();

		AuthenticationType type = buffer.readEnumConstant(AuthenticationType.class);
		UUID identity = buffer.readUUID();
		String name = buffer.readString();

		cloud.getLogger().trace("Received authentication from {}: type={}, name={}", channel, type, name);

		if (!cloud.getConfig().getIdentity().equals(identity)) {
			cloud.getLogger().info("Authentication for node {} with identity {} was rejected: {}", name, identity, channel);
			channel.sendPacket(new NetworkAuthResponsePacket(false, "authentication failed"));
			channel.close();
			return;
		}

		switch (type) {

			case NODE: {
				if (cloud.getNodeManager().getNodeInfos().stream().anyMatch(info -> info.getName().equalsIgnoreCase(name))) {
					cloud.getLogger().warn("{} tried to authenticate again with node name '{}'", channel, name);
					channel.sendPacket(new NetworkAuthResponsePacket(false, "node address already registered"));
					return;
				}
				if (cloud.getNodeManager().getNodeInfos().stream().anyMatch(info -> info.getName().equalsIgnoreCase(name))) {
					cloud.getLogger().warn("{} tried to register with duplicate node name '{}'", channel, name);
					channel.sendPacket(new NetworkAuthResponsePacket(false, "node already registered"));
					channel.close();
					return;
				}

				NodeInfo info = new NodeInfo(name, channel.getClientAddress());
				NodeServer server = new DefaultNodeServer(info, channel);
				cloud.getNodeManager().getNodeServers().add(server);

				cloud.getLogger().info("Node '{}' has connected successfully", name);
				channel.sendPacket(new NetworkAuthResponsePacket(true, "successful"));
				channel.sendPacket(ConfigInitPacket.create());

				// TODO
				Thread.sleep(3000);
				cloud.getServiceFactory().createService(cloud.getServiceConfigManager().getTask("Proxy"));

				return;
			}

			case SERVICE: {
				CloudService service = cloud.getServiceManager().getServiceByName(name);
				if (service == null) {
					cloud.getLogger().warn("{} tried to register with unknown service name '{}'", channel, name);
					channel.sendPacket(new NetworkAuthResponsePacket(false, "unknown service"));
					channel.close();
					return;
				}

				service.setChannel(channel);

				cloud.getLogger().info("Service '{}' has connected successfully", name);
				channel.sendPacket(new NetworkAuthResponsePacket(true, "successful"));

				return;
			}

		}

	}

}
