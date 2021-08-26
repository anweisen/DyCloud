package net.anweisen.cloud.master.node;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.node.NodeInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultNodeServerManager implements NodeServerManager {

	private final Set<NodeServer> servers = new CopyOnWriteArraySet<>();

	@Nonnull
	@Override
	public Collection<NodeServer> getNodeServers() {
		return servers;
	}

	@Nullable
	@Override
	public NodeServer getNodeServer(@Nonnull String name) {
		for (NodeServer server : servers) {
			if (server.getInfo().getName().equalsIgnoreCase(name))
				return server;
		}
		return null;
	}

	@Nullable
	@Override
	public NodeServer getNodeServer(@Nonnull SocketChannel channel) {
		for (NodeServer server : servers) {
			if (server.getChannel().equals(channel))
				return server;
		}
		return null;
	}

	@Nonnull
	@Override
	public List<NodeInfo> getNodeInfos() {
		return servers.stream().map(NodeServer::getInfo).collect(Collectors.toList());
	}
}
