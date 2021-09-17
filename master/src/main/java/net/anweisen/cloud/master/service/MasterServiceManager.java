package net.anweisen.cloud.master.service;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.service.ServiceReadyEvent;
import net.anweisen.cloud.driver.service.DefaultServiceManager;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.service.specific.CloudService;
import net.anweisen.cloud.master.service.specific.MasterServiceController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterServiceManager extends DefaultServiceManager implements CloudServiceManager {

	private final Map<UUID, CloudService> services = new ConcurrentHashMap<>();

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
		Collection<ServiceInfo> infos = new ArrayList<>(services.size());
		for (CloudService service : services.values()) {
			infos.add(service.getInfo());
		}
		return infos;
	}

	@Nonnull
	@Override
	public Collection<CloudService> getServices() {
		return Collections.unmodifiableCollection(services.values());
	}

	@Nullable
	@Override
	public CloudService getServiceByUniqueId(@Nonnull UUID uniqueId) {
		return services.get(uniqueId);
	}

	@Override
	public void registerService(@Nonnull CloudService service) {
		services.put(service.getInfo().getUniqueId(), service);
	}

	@Override
	protected void updateServiceInfoInternally(@Nonnull ServiceInfo newServiceInfo) {
		CloudService service = getServiceByUniqueId(newServiceInfo.getUniqueId());
		if (service == null) {
			CloudDriver.getInstance().getLogger().warn("Tried to update ServiceInfo " + newServiceInfo + ", but the service is no longer registered");
			return;
		}
		boolean readyEvent = !service.getInfo().isReady() && newServiceInfo.isReady();
		service.setInfo(newServiceInfo);
		if (readyEvent) CloudDriver.getInstance().getEventManager().callEvent(new ServiceReadyEvent(newServiceInfo));
	}

	@Override
	protected void unregisterServiceInfoInternally(@Nonnull ServiceInfo serviceInfo) {
		services.remove(serviceInfo.getUniqueId());
	}

	@Override
	public void setServiceInfos(@Nonnull Collection<? extends ServiceInfo> services) {
		throw new UnsupportedOperationException("Cannot setServiceInfos on the master");
	}
}
