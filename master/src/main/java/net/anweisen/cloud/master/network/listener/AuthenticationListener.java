package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket.AuthenticationType;
import net.anweisen.cloud.driver.network.packet.def.ConfigInitPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.PublishType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.network.packets.NetworkAuthResponsePacket;
import net.anweisen.cloud.master.node.DefaultNodeServer;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class AuthenticationListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();

		Buffer buffer = packet.getBuffer();

		AuthenticationType type = buffer.readEnumConstant(AuthenticationType.class);
		UUID identity = buffer.readUUID();
		String name = buffer.readString();

		cloud.getLogger().debug("Received authentication from {}: type={}, name={}", channel, type, name);

		if (!cloud.getConfig().getIdentity().equals(identity)) {
			cloud.getLogger().info("Authentication for '{}' with identity {} was rejected: {}", name, identity, channel);
			channel.sendPacket(new NetworkAuthResponsePacket(false, "authentication failed"));
			channel.close();
			return;
		}

		switch (type) {

			case NODE: {
				if (cloud.getNodeManager().getNodeInfos().stream().anyMatch(info -> info.getAddress().equals(channel.getClientAddress()))) {
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

				Collection<String> subnetIps = buffer.readStringCollection();
				server.getSubnetIps().addAll(subnetIps);

				cloud.getLogger().info("Node '{}' has connected successfully", name);
				channel.sendPacket(new NetworkAuthResponsePacket(true, "successful"));
				channel.sendPacket(ConfigInitPacket.create());

				// TODO
				Thread.sleep(500);
				cloud.getServiceFactory().createService(cloud.getServiceConfigManager().getTask("Lobby"));
				cloud.getServiceFactory().createService(cloud.getServiceConfigManager().getTask("Lobby"));
				cloud.getServiceFactory().createService(cloud.getServiceConfigManager().getTask("Proxy"));
				break;
			}

			case CORD: {
				if (cloud.getCordManager().getCordInfos().stream().anyMatch(info -> info.getClientAddress().equals(channel.getClientAddress()))) {
					cloud.getLogger().warn("{} tried to authenticate again with cord name '{}'", channel, name);
					channel.sendPacket(new NetworkAuthResponsePacket(false, "cord address already registered"));
					return;
				}
				if (cloud.getCordManager().getCordInfos().stream().anyMatch(info -> info.getName().equalsIgnoreCase(name))) {
					cloud.getLogger().warn("{} tried to register with duplicate cord name '{}'", channel, name);
					channel.sendPacket(new NetworkAuthResponsePacket(false, "cord already registered"));
					channel.close();
					return;
				}

				HostAndPort proxyAddress = buffer.readObject(HostAndPort.class);

				CordInfo info = new CordInfo(name, channel.getClientAddress(), proxyAddress);
				CordServer server = new DefaultCordServer(info, channel);
				cloud.getCordManager().getCordServers().add(server);

				cloud.getLogger().info("Cord '{}' has connected successfully", name);
				channel.sendPacket(new NetworkAuthResponsePacket(true, "successful"));
				channel.sendPacket(ConfigInitPacket.create());
				break;
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
				cloud.publishUpdate(PublishType.CONNECTED, service.getInfo());
				cloud.getServiceManager().handleServiceUpdate(PublishType.CONNECTED, service.getInfo());

				cloud.getLogger().info("Service '{}' has connected successfully", name);
				channel.sendPacket(new NetworkAuthResponsePacket(true, "successful"));
				channel.sendPacket(ConfigInitPacket.create());

				break;
			}

			default:
				cloud.getLogger().info("Authentication for '{}' with identity {} was a invalid type: {}", name, identity, channel);
				channel.sendPacket(new NetworkAuthResponsePacket(false, "invalid type"));
				channel.close();
				return;

		}

	}

}
