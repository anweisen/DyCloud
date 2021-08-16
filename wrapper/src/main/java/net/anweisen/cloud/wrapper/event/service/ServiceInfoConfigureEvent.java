package net.anweisen.cloud.wrapper.event.service;

import net.anweisen.cloud.driver.event.service.ServiceEvent;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceInfoConfigureEvent extends ServiceEvent {

	public ServiceInfoConfigureEvent(@Nonnull ServiceInfo serviceInfo) {
		super(serviceInfo);
	}

}
