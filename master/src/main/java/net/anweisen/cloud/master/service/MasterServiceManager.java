package net.anweisen.cloud.master.service;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.service.DefaultServiceManager;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterServiceManager extends DefaultServiceManager implements CloudServiceManager {

	private final Collection<CloudService> services = new CopyOnWriteArrayList<>();

	@Nonnull
	@Override
	public ServiceController getController(@Nonnull ServiceInfo serviceInfo) {
		CloudService service = getServiceByUniqueId(serviceInfo.getUniqueId());
		Preconditions.checkNotNull(service, "Service no longer registered (state: " + serviceInfo.getState() + ")");
		return getController(service);
	}

	@Nonnull
	@Override
	public ServiceController getController(@Nonnull CloudService service) {
		return new MasterServiceController(service);
	}

	@Nonnull
	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		return services.stream().map(CloudService::getInfo).collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public Collection<CloudService> getServices() {
		return services;
	}

	@Nonnull
	@Override
	public Collection<CloudService> getServicesByTask(@Nonnull String taskName) {
		return services.stream().filter(service -> service.getInfo().getTaskName().equals(taskName)).collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public Collection<CloudService> getServicesByNode(@Nonnull String nodeName) {
		return services.stream().filter(service -> service.getInfo().getNodeName().equals(nodeName)).collect(Collectors.toList());
	}

	@Nullable
	@Override
	public CloudService getServiceByUniqueId(@Nonnull UUID uniqueId) {
		return services.stream().filter(service -> service.getInfo().getUniqueId().equals(uniqueId)).findFirst().orElse(null);
	}

	@Nullable
	@Override
	public CloudService getServiceByName(@Nonnull String serviceName) {
		return services.stream().filter(service -> service.getInfo().getName().equals(serviceName)).findFirst().orElse(null);
	}

	@Nullable
	@Override
	public CloudService getServiceByChannel(@Nonnull SocketChannel channel) {
		return services.stream().filter(service -> service.getChannel() == channel).findFirst().orElse(null);
	}

	@Override
	protected void updateServiceInfoInternally(@Nonnull ServiceInfo newServiceInfo) {
		services.stream()
			.filter(service -> service.getInfo().getUniqueId().equals(newServiceInfo.getUniqueId()))
			.findFirst()
			.ifPresent(service -> service.setInfo(newServiceInfo));
	}


	@Override
	protected void unregisterServiceInfoInternally(@Nonnull ServiceInfo serviceInfo) {
		services.removeIf(service -> service.getInfo().getUniqueId().equals(serviceInfo.getUniqueId()));
	}

	@Override
	public void setServiceInfos(@Nonnull Collection<? extends ServiceInfo> services) {
		throw new UnsupportedOperationException("Cannot setServiceInfos on the master");
	}
}
