package net.anweisen.cloud.master.network.handler;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.network.PacketReceiveEvent;
import net.anweisen.cloud.driver.event.network.SocketChannelCloseEvent;
import net.anweisen.cloud.driver.event.network.SocketChannelConnectEvent;
import net.anweisen.cloud.driver.network.InternalQueryResponseManager;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.def.NodeInfoPublishPacket.NodePublishType;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.ServicePublishType;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.cord.CordServer;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;

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
			cloud.getLogger().info("{} is not in the ip whitelist {}! Closing channel..", channel.getClientAddress().getHost(), cloud.getConfig().getIpWhitelist());
			try {
				channel.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}

		SocketChannelConnectEvent event = cloud.getEventManager().callEvent(new SocketChannelConnectEvent(channel));
		if (event.isCancelled()) {
			cloud.getLogger().info("Connect event of {} was cancelled! Closing channel..", channel.getClientAddress());
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
			cloud.publishUpdate(ServicePublishType.DISCONNECTED, service.getInfo());
			cloud.getServiceManager().handleServiceUpdate(ServicePublishType.DISCONNECTED, service.getInfo());
			return;
		}

		NodeServer node = cloud.getNodeManager().getNodeServer(channel);
		if (node != null) {
			cloud.getLogger().warn("Node '{}' has disconnected", node.getInfo().getName());
			cloud.publishUpdate(NodePublishType.DISCONNECTED, node.getInfo());
			cloud.getNodeManager().handleNodeUpdate(NodePublishType.DISCONNECTED, node.getInfo());
			cloud.getNodeManager().unregisterNode(node);
			return;
		}

		// TODO publish
		CordServer cord = cloud.getCordManager().getCordServer(channel);
		if (cord != null) {
			cloud.getLogger().warn("Cord '{}' has disconnected", cord.getInfo().getName());
			cloud.getCordManager().getCordServers().remove(cord);
			return;
		}

		cloud.getLogger().warn("Channel[client={} server={}] was neither a node/service/cord", channel.getClientAddress(), channel.getServerAddress());
		cloud.getLogger().extended("Nodes:");
		cloud.getNodeManager().getNodeServers().stream().map(current -> current.getInfo().getName() + " | " + current.getChannel()).forEach(line -> cloud.getLogger().extended("=> {}", line));
		cloud.getLogger().extended("Cords:");
		cloud.getCordManager().getCordServers().stream().map(current -> current.getInfo().getName() + " | " + current.getChannel()).forEach(line -> cloud.getLogger().extended("=> {}", line));
		cloud.getLogger().extended("Services:");
		cloud.getServiceManager().getServices().stream().map(current -> current.getInfo().getName() + " | " + current.getChannel()).forEach(line -> cloud.getLogger().extended("=> {}", line));
	}

	protected boolean inWhitelist(@Nonnull SocketChannel channel) {
		String ipAddress = channel.getClientAddress().getHost();
		if (ipAddress.equals("0.0.0.0") || ipAddress.equals("127.0.0.1") || ipAddress.equals("localhost"))
			return true;

		if (CloudMaster.getInstance().getConfig().getIpWhitelist().contains(ipAddress))
			return true;

		for (NodeInfo node : CloudMaster.getInstance().getNodeManager().getNodeInfos()) {
			if (node.getSubnet().matches(ipAddress))
				return true;
			if (node.getGateway().equals(ipAddress))
				return true;
		}

		return false;
	}

}
