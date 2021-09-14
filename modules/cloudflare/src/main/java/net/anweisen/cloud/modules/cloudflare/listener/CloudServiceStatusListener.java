package net.anweisen.cloud.modules.cloudflare.listener;

import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.ServiceReadyEvent;
import net.anweisen.cloud.driver.event.service.ServiceStoppedEvent;
import net.anweisen.cloud.driver.event.service.ServiceUnregisteredEvent;
import net.anweisen.cloud.modules.cloudflare.CloudCloudflareModule;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudServiceStatusListener {

	@EventListener
	public void onServiceReady(@Nonnull ServiceReadyEvent event) {
		if (!event.getServiceInfo().getEnvironment().isProxy()) return;
		CloudCloudflareModule.getInstance().createCloudflareDnsServiceEntries(event.getServiceInfo());
	}

	@EventListener
	public void onServiceStopped(@Nonnull ServiceStoppedEvent event) {
		CloudCloudflareModule.getInstance().getCloudflareAPI().deleteAllRecords(event.getServiceInfo().getUniqueId());
	}

	@EventListener
	public void onServiceUnregistered(@Nonnull ServiceUnregisteredEvent event) {
		CloudCloudflareModule.getInstance().getCloudflareAPI().deleteAllRecords(event.getServiceInfo().getUniqueId());
	}

}
