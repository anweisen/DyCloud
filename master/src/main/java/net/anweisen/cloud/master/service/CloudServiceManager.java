package net.anweisen.cloud.master.service;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CloudServiceManager extends ServiceManager {

	@Nonnull
	ServiceController getController(@Nonnull CloudService service);

	@Nonnull
	Collection<CloudService> getServices();

	@Nonnull
	Collection<CloudService> getServicesByTask(@Nonnull String taskName);

	@Nonnull
	Collection<CloudService> getServicesByNode(@Nonnull String nodeName);

	@Nullable
	CloudService getServiceByName(@Nonnull String serviceName);

	@Nullable
	CloudService getServiceByUniqueId(@Nonnull UUID uniqueId);

	@Nullable
	CloudService getServiceByChannel(@Nonnull SocketChannel channel);

}
