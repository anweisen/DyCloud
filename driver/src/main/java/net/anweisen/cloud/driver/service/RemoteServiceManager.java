package net.anweisen.cloud.driver.service;

import net.anweisen.cloud.driver.network.request.NetworkingApiUser;
import net.anweisen.cloud.driver.network.request.RequestType;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteServiceManager extends DefaultServiceManager implements NetworkingApiUser {

	private final Set<ServiceInfo> services = new LinkedHashSet<>();

	@Override
	protected void updateServiceInfoInternally(@Nonnull ServiceInfo newServiceInfo) {
		unregisterServiceInfoInternally(newServiceInfo);
		services.add(newServiceInfo);
	}

	@Override
	protected void unregisterServiceInfoInternally(@Nonnull ServiceInfo serviceInfo) {
		services.removeIf(service -> service.getUniqueId().equals(serviceInfo.getUniqueId()));
	}

	@Nonnull
	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		return Collections.unmodifiableCollection(services);
	}

	@Override
	public void setServiceInfos(@Nonnull Collection<? extends ServiceInfo> services) {
		this.services.clear();
		this.services.addAll(services);
	}
}
