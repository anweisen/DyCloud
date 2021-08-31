package net.anweisen.cloud.modules.cloudflare.listener;

import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.ServiceStartedEvent;
import net.anweisen.cloud.driver.event.service.ServiceStoppedEvent;
import net.anweisen.cloud.modules.cloudflare.CloudCloudflareModule;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudServiceStatusListener {

	@EventListener
	public void onServiceStarted(@Nonnull ServiceStartedEvent event) {
		CloudCloudflareModule.getInstance().createCloudflareDnsServiceEntries(event.getServiceInfo());
	}

	@EventListener
	public void onServiceStopped(@Nonnull ServiceStoppedEvent event) {
		CloudCloudflareModule.getInstance().getCloudflareAPI().deleteAllRecords(event.getServiceInfo().getUniqueId());
	}

}
