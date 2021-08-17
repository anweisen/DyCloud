package net.anweisen.cloud.master.network.handler;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.network.PacketReceiveEvent;
import net.anweisen.cloud.driver.event.network.SocketChannelCloseEvent;
import net.anweisen.cloud.driver.event.network.SocketChannelConnectEvent;
import net.anweisen.cloud.driver.network.InternalQueryResponseManager;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.PublishType;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SocketChannelServerHandler implements SocketChannelHandler {

	@Override
	public void handleChannelInitialize(@Nonnull SocketChannel channel) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();

		cloud.getLogger().info("Channel[client={} server={}] was successfully connected", channel.getClientAddress(), channel.getServerAddress());
		cloud.getLogger().debug("Currently there {} {} channel{} connected to this socket", cloud.getSocketComponent().getChannels().size() == 1 ? "is" : "are", cloud.getSocketComponent().getChannels().size(), cloud.getSocketComponent().getChannels().size() == 1 ? "" : "s");

		if (!inWhitelist(channel)) {
			try {
				channel.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}

		SocketChannelConnectEvent event = cloud.getEventManager().callEvent(new SocketChannelConnectEvent(channel));
		if (event.isCancelled()) {
			try {
				channel.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}

	}

	@Override
	public boolean handlePacketReceive(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		if (InternalQueryResponseManager.handleIncomingPacket(channel, packet))
			return false;

		return !CloudDriver.getInstance().getEventManager().callEvent(new PacketReceiveEvent(channel, packet)).isCancelled();
	}

	@Override
	public void handleChannelClose(@Nonnull SocketChannel channel) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();

		cloud.getEventManager().callEvent(new SocketChannelCloseEvent(channel));
		cloud.getLogger().info("Channel[client={} server={}] was closed", channel.getClientAddress(), channel.getServerAddress());

		CloudService service = cloud.getServiceManager().getServiceByChannel(channel);
		if (service != null) {
			cloud.getLogger().info("Service '{}' has disconnected", service.getInfo().getName());
			cloud.publishUpdate(PublishType.DISCONNECTED, service.getInfo());
			cloud.getServiceManager().handleServiceUpdate(PublishType.DISCONNECTED, service.getInfo());
			return;
		}

		NodeServer nodeServer = cloud.getNodeManager().getNodeServer(channel);
		if (nodeServer != null) {
			cloud.getLogger().warn("Node '{}' has disconnected", nodeServer.getInfo().getName());
			cloud.getNodeManager().getNodeServers().remove(nodeServer);
			return;
		}

		cloud.getLogger().warn("Channel[client={} server={}] was neither a node or service", channel.getClientAddress(), channel.getServerAddress());
		cloud.getLogger().extended("Nodes: {}", cloud.getNodeManager().getNodeServers().stream().map(current -> current.getInfo().getName() + " | " + current.getChannel()).collect(Collectors.joining("   ")));
		cloud.getLogger().extended("Services: {}", cloud.getServiceManager().getServices().stream().map(current -> current.getInfo().getName() + " | " + current.getChannel()).collect(Collectors.joining("   ")));
	}

	protected boolean inWhitelist(@Nonnull SocketChannel channel) {
		for (String whitelistedIp : CloudMaster.getInstance().getConfig().getIpWhitelist()) {
			if (channel.getClientAddress().getHost().equals(whitelistedIp))
				return true;
		}

		return false;
	}

}
