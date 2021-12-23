package net.anweisen.cloud.driver.event.service;

import net.anweisen.cloud.driver.event.DefaultEvent;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class ServiceEvent extends DefaultEvent {

	protected final ServiceInfo serviceInfo;

	public ServiceEvent(@Nonnull ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	@Nonnull
	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[service=" + serviceInfo.getName() + "]";
	}
}
