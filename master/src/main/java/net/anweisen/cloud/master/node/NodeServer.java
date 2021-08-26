package net.anweisen.cloud.master.node;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface NodeServer {

	@Nonnull
	NodeInfo getInfo();

	@Nonnull
	SocketChannel getChannel();

	boolean isAvailable();

	@Nonnull
	Collection<CloudService> findServices();

	@Nonnull
	Collection<ServiceInfo> findServiceInfos();

}
