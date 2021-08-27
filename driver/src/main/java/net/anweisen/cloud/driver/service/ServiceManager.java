package net.anweisen.cloud.driver.service;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.PublishType;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

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
		return getServiceInfos().stream().filter(info -> info.getTaskName().equals(taskName)).collect(Collectors.toList());
	}

	@Nonnull
	default Collection<ServiceInfo> getServiceInfosByNode(@Nonnull String nodeName) {
		return getServiceInfos().stream().filter(info -> info.getNodeName().equals(nodeName)).collect(Collectors.toList());
	}

	@Nullable
	default ServiceInfo getServiceInfoByName(@Nonnull String serviceName) {
		return getServiceInfos().stream().filter(info -> info.getName().equals(serviceName)).findFirst().orElse(null);
	}

	@Nullable
	default ServiceInfo getServiceInfoByUUID(@Nonnull UUID uniqueId) {
		return getServiceInfos().stream().filter(info -> info.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
	}

	void handleServiceUpdate(@Nonnull PublishType type, @Nonnull ServiceInfo info);

	void setServiceInfos(@Nonnull Collection<? extends ServiceInfo> services);

}
