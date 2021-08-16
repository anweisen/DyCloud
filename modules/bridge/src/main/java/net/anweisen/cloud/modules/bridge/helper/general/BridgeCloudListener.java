package net.anweisen.cloud.modules.bridge.helper.general;

import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.*;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.wrapper.event.service.ServiceInfoConfigureEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BridgeCloudListener {

	@EventListener
	public void onInfoConfigure(@Nonnull ServiceInfoConfigureEvent event) {
		event.getServiceInfo().setReady();
	}

	@EventListener
	public void onServiceRegistered(@Nonnull ServiceRegisteredEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BridgeHelper.cacheService(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceConnected(@Nonnull ServiceConnectedEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BridgeHelper.cacheService(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceDisconnected(@Nonnull ServiceDisconnectedEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BridgeHelper.cacheService(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceUpdate(@Nonnull ServiceUpdateEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BridgeHelper.cacheService(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceStarted(@Nonnull ServiceStartedEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BridgeHelper.cacheService(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceStopped(@Nonnull ServiceStoppedEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BridgeHelper.cacheService(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceUnregistered(@Nonnull ServiceUnregisteredEvent event) {
		BridgeHelper.removeCachedService(event.getServiceInfo().getName());
	}

}
