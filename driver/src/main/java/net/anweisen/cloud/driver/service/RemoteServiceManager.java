package net.anweisen.cloud.driver.service;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.service.ServiceReadyEvent;
import net.anweisen.cloud.driver.network.NetworkingApiUser;
import net.anweisen.cloud.driver.service.specific.RemoteServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteServiceManager extends DefaultServiceManager implements NetworkingApiUser {

	private final Map<UUID, ServiceInfo> services = new ConcurrentHashMap<>();

	@Nonnull
	@Override
	public ServiceController getController(@Nonnull ServiceInfo service) {
		return new RemoteServiceController(service);
	}

	@Override
	protected void updateServiceInfoInternally(@Nonnull ServiceInfo newServiceInfo) {
		ServiceInfo oldServiceInfo = services.put(newServiceInfo.getUniqueId(), newServiceInfo);
		if (oldServiceInfo != null && !oldServiceInfo.isReady() && newServiceInfo.isReady())
			CloudDriver.getInstance().getEventManager().callEvent(new ServiceReadyEvent(newServiceInfo));
	}

	@Override
	protected void unregisterServiceInfoInternally(@Nonnull ServiceInfo serviceInfo) {
		services.remove(serviceInfo.getUniqueId());
	}

	@Nonnull
	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		return Collections.unmodifiableCollection(services.values());
	}

	@Override
	public void setServiceInfos(@Nonnull Collection<? extends ServiceInfo> services) {
		this.services.clear();
		for (ServiceInfo service : services) {
			this.services.put(service.getUniqueId(), service);
		}
	}

	@Override
	public void registerService(@Nonnull ServiceInfo service) {
		services.put(service.getUniqueId(), service);
	}
}
