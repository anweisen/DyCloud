package net.anweisen.cloud.modules.cloudflare.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.node.NodeConnectedEvent;
import net.anweisen.cloud.driver.event.node.NodeDisconnectedEvent;
import net.anweisen.cloud.modules.cloudflare.CloudCloudflareModule;
import net.anweisen.cloud.modules.cloudflare.api.dns.DnsRecordDetail;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudNodeConnectionListener {

	@EventListener
	public void onNodeConnected(@Nonnull NodeConnectedEvent event) {
		CloudCloudflareModule.getInstance().createCloudflareDnsNodeEntries(event.getNode());
	}

	@EventListener
	public void onNodeDisconnected(@Nonnull NodeDisconnectedEvent event) {
		Collection<DnsRecordDetail> records = CloudCloudflareModule.getInstance().getCloudflareAPI().getCreatedRecords(CloudDriver.getInstance().getConfig().getIdentity());
		for (DnsRecordDetail record : records) {
			if (record.getDnsRecord().getName().contains(event.getNode().getName().toLowerCase()))
				CloudCloudflareModule.getInstance().getCloudflareAPI().deleteRecord(record);
		}
	}

}
