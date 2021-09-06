package net.anweisen.cloud.driver.event.service;

import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceReadyEvent extends ServiceEvent {

	public ServiceReadyEvent(@Nonnull ServiceInfo serviceInfo) {
		super(serviceInfo);
	}
}
