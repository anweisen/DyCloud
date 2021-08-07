package net.anweisen.cloud.driver.event.service;

import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceRegisteredEvent extends ServiceEvent {

	public ServiceRegisteredEvent(@Nonnull ServiceInfo serviceInfo) {
		super(serviceInfo);
	}
}
