package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket.AuthenticationPayload;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationResponsePacket;
import net.anweisen.cloud.driver.network.packet.def.NodePublishPacket.NodePublishPayload;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.master.CloudMaster;
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

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();

		PacketBuffer buffer = packet.getBuffer();

		AuthenticationPayload payload = buffer.readEnum(AuthenticationPayload.class);
		UUID identity = buffer.readUniqueId();

		cloud.getLogger().debug("Received authentication from {}: type={}", channel, payload);

		if (!cloud.getConfig().getIdentity().equals(identity)) {
			cloud.getLogger().info("Authentication for some {} with identity {} was rejected: {}", payload, identity, channel);
			channel.sendPacket(new AuthenticationResponsePacket(false, "authentication failed"));
			channel.close();
			return;
		}

		switch (payload) {

			case NODE: {
				String name = buffer.readString();
				if (cloud.getNodeManager().getNodeInfos().stream().anyMatch(info -> info.getAddress().equals(channel.getClientAddress()))) {
					cloud.getLogger().warn("{} tried to authenticate again with node name '{}'", channel, name);
					channel.sendPacket(new AuthenticationResponsePacket(false, "node address already registered"));
					return;
				}
				if (cloud.getNodeManager().getNodeInfos().stream().anyMatch(info -> info.getName().equalsIgnoreCase(name))) {
					cloud.getLogger().warn("{} tried to register with duplicate node name '{}'", channel, name);
					channel.sendPacket(new AuthenticationResponsePacket(false, "node already registered"));
					channel.close();
					return;
				}

				NodeInfo info = new NodeInfo(name, channel.getClientAddress(), buffer.readString(), buffer.readString());
				NodeServer server = new DefaultNodeServer(info, channel);
				cloud.getNodeManager().registerNode(server);

				cloud.getLogger().info("Node '{}' has connected successfully", name);
				cloud.getLogger().extended("=> Subnet range for {}: {}", name, info.getSubnet());
				cloud.getLogger().extended("=> Gateway ip for {}: {}", name, info.getGateway());
				cloud.getNodeManager().handleNodeUpdate(NodePublishPayload.CONNECTED, info);
				cloud.publishUpdate(NodePublishPayload.CONNECTED, info);
				channel.sendPacket(new AuthenticationResponsePacket(true, "successful"));
				break;
			}

			case SERVICE: {
				UUID uniqueId = buffer.readUniqueId();
				CloudService service = cloud.getServiceManager().getServiceByUniqueId(uniqueId);
				if (service == null) {
					cloud.getLogger().warn("{} tried to register with unknown service '{}'", channel, uniqueId);
					channel.sendPacket(new AuthenticationResponsePacket(false, "unknown service"));
					channel.close();
					return;
				}

				service.setChannel(channel);
				service.getInfo().setConnected(true);
				cloud.publishUpdate(ServicePublishPayload.CONNECTED, service.getInfo());
				cloud.getServiceManager().handleServiceUpdate(ServicePublishPayload.CONNECTED, service.getInfo());

				cloud.getLogger().info("Service '{}' has connected successfully", service.getInfo().getName());
				channel.sendPacket(new AuthenticationResponsePacket(true, "successful"));
				break;
			}

			default: {
				cloud.getLogger().info("Authentication for '{}' with identity {} was a invalid type", channel, identity);
				channel.sendPacket(new AuthenticationResponsePacket(false, "invalid type"));
				channel.close();
				return;
			}

		}

	}

}
