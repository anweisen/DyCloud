package net.anweisen.cloud.driver.service;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishPayload;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getServiceManager()
 */
public interface ServiceManager {

	@Nonnull
	ServiceController getController(@Nonnull ServiceInfo service);

	@Nonnull
	Collection<ServiceInfo> getServiceInfos();

	@Nonnull
	default Collection<ServiceInfo> getServiceInfosByTask(@Nonnull String taskName) {
		Collection<ServiceInfo> services = new ArrayList<>(1);
		for (ServiceInfo service : getServiceInfos()) {
			if (service.getTaskName().equals(taskName))
				services.add(service);
		}
		return services;
	}

	@Nonnull
	default Collection<ServiceInfo> getServiceInfosByNode(@Nonnull String nodeName) {
		Collection<ServiceInfo> services = new ArrayList<>(1);
		for (ServiceInfo service : getServiceInfos()) {
			if (service.getNodeName().equals(nodeName))
				services.add(service);
		}
		return services;
	}

	@Nullable
	default ServiceInfo getServiceInfoByName(@Nonnull String serviceName) {
		for (ServiceInfo service : getServiceInfos()) {
			if (service.getName().equals(serviceName))
				return service;
		}
		return null;
	}

	@Nullable
	default ServiceInfo getServiceInfoByUniqueId(@Nonnull UUID uniqueId) {
		for (ServiceInfo service : getServiceInfos()) {
			if (service.getUniqueId().equals(uniqueId))
				return service;
		}
		return null;
	}

	@Nullable
	default ServiceInfo getServiceInfoByDockerId(@Nonnull String dockerContainerId) {
		for (ServiceInfo service : getServiceInfos()) {
			if (dockerContainerId.equals(service.getDockerContainerId()))
				return service;
		}
		return null;
	}

	void handleServiceUpdate(@Nonnull ServicePublishPayload payload, @Nonnull ServiceInfo info);

	void setServiceInfos(@Nonnull Collection<? extends ServiceInfo> services);

	void registerService(@Nonnull ServiceInfo service);

}
