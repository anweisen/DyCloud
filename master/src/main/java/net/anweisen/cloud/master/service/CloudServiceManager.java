package net.anweisen.cloud.master.service;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
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
	default Collection<CloudService> getServicesByTask(@Nonnull String taskName) {
		Collection<CloudService> services = new ArrayList<>();
		for (CloudService service : getServices()) {
			if (service.getInfo().getTaskName().equalsIgnoreCase(taskName))
				services.add(service);
		}
		return services;
	}

	@Nonnull
	default Collection<CloudService> getServicesByNode(@Nonnull String nodeName) {
		Collection<CloudService> services = new ArrayList<>();
		for (CloudService service : getServices()) {
			if (service.getInfo().getNodeName().equalsIgnoreCase(nodeName))
				services.add(service);
		}
		return services;
	}

	@Nonnull
	default Collection<CloudService> getServicesByType(@Nonnull ServiceType type) {
		Collection<CloudService> services = new ArrayList<>();
		for (CloudService service : getServices()) {
			if (service.getInfo().getEnvironment().getServiceType() == type)
				services.add(service);
		}
		return services;
	}

	@Nullable
	default CloudService getServiceByName(@Nonnull String serviceName) {
		for (CloudService service : getServices()) {
			if (service.getInfo().getName().equalsIgnoreCase(serviceName))
				return service;
		}
		return null;
	}

	@Nullable
	default CloudService getServiceByUniqueId(@Nonnull UUID uniqueId) {
		for (CloudService service : getServices()) {
			if (service.getInfo().getUniqueId().equals(uniqueId))
				return service;
		}
		return null;
	}

	@Nullable
	default CloudService getServiceByChannel(@Nonnull SocketChannel channel) {
		for (CloudService service : getServices()) {
			if (channel.equals(service.getChannel()))
				return service;
		}
		return null;
	}

	void registerService(@Nonnull CloudService service);

	@Override
	default void registerService(@Nonnull ServiceInfo service) {
		throw new UnsupportedOperationException("Use CloudServiceManager.registerService(CloudService) on the master");
	}

}
