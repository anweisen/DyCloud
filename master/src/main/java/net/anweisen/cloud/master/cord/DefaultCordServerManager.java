package net.anweisen.cloud.master.cord;

import net.anweisen.cloud.driver.cord.CordInfo;
import net.anweisen.cloud.driver.network.SocketChannel;

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
public class DefaultCordServerManager implements CordServerManager {

	private final Set<CordServer> servers = new CopyOnWriteArraySet<>();

	@Nonnull
	@Override
	public Collection<CordServer> getCordServers() {
		return servers;
	}

	@Nullable
	@Override
	public CordServer getCordServer(@Nonnull String name) {
		for (CordServer server : servers) {
			if (server.getInfo().getName().equalsIgnoreCase(name))
				return server;
		}
		return null;
	}

	@Nullable
	@Override
	public CordServer getCordServer(@Nonnull SocketChannel channel) {
		for (CordServer server : servers) {
			if (server.getChannel().equals(channel))
				return server;
		}
		return null;
	}

	@Nonnull
	@Override
	public List<CordInfo> getCordInfos() {
		return servers.stream().map(CordServer::getInfo).collect(Collectors.toList());
	}
}
