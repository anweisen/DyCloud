package net.anweisen.cloud.driver.service;

import com.google.common.collect.Sets;
import net.anweisen.cloud.driver.network.request.NetworkingApiUser;
import net.anweisen.cloud.driver.service.specific.RemoteServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteServiceManager extends DefaultServiceManager implements NetworkingApiUser {

	private final Set<ServiceInfo> services = Sets.newConcurrentHashSet();

	@Nonnull
	@Override
	public ServiceController getController(@Nonnull ServiceInfo service) {
		return new RemoteServiceController(service);
	}

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

	@Override
	public void registerService(@Nonnull ServiceInfo service) {
		services.add(service);
	}
}
