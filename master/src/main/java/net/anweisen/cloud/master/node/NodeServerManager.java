package net.anweisen.cloud.master.node;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.node.NodeManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface NodeServerManager extends NodeManager {

	@Nonnull
	Collection<NodeServer> getNodeServers();

	@Nullable
	NodeServer getNodeServer(@Nonnull String name);

	@Nullable
	NodeServer getNodeServer(@Nonnull SocketChannel channel);

}
