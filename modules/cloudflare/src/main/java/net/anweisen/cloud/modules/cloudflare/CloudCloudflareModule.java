package net.anweisen.cloud.modules.cloudflare;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.cloudflare.api.CloudflareAPI;
import net.anweisen.cloud.modules.cloudflare.api.dns.DefaultDnsRecord;
import net.anweisen.cloud.modules.cloudflare.api.dns.DnsRecordDetail;
import net.anweisen.cloud.modules.cloudflare.api.dns.DnsType;
import net.anweisen.cloud.modules.cloudflare.api.dns.SrvRecord;
import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfig;
import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntry;
import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntryGroup;
import net.anweisen.cloud.modules.cloudflare.listener.CloudNodeConnectionListener;
import net.anweisen.cloud.modules.cloudflare.listener.CloudServiceStatusListener;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudCloudflareModule extends CloudModule {

	private static CloudCloudflareModule instance;

	private CloudflareConfig config;
	private CloudflareAPI api;

	@Override
	protected void onLoad() {
		instance = this;

		loadConfig();
		if (!getEnabled(false)) return;
		initCloudflareApi();
		createCloudflareDnsNodeEntries();
		initListeners();
	}

	private void initListeners() {
		registerListeners(new CloudNodeConnectionListener(), new CloudServiceStatusListener());
	}

	private void loadConfig() {
		config = getConfig().get("config", CloudflareConfig.class);
		getLogger().debug("Loaded config {}", config);
		if (config == null)
			getConfig().set("config", config = new CloudflareConfig(
				Collections.singletonList(
					new CloudflareConfigEntry(
						false,
						"user@example.com",
						"apiToken",
						"zoneId",
						"example.com",
						Collections.singletonList(
							new CloudflareConfigEntryGroup(
								"Proxy",
								"",
								1,
								1
							)
						)
					)
				)
			)).save();
	}

	private void initCloudflareApi() {
		api = new CloudflareAPI();
	}

	private void createCloudflareDnsNodeEntries() {
		getDriver().getNodeManager().getNodeInfos().forEach(this::createCloudflareDnsNodeEntries);
	}

	public void createCloudflareDnsNodeEntries(@Nonnull NodeInfo node) {
		for (CloudflareConfigEntry entry : config.getEntries()) {
			if (!entry.isEnabled()) continue;

			String endpoint = getDriver().getConfig().getIdentity() + "." + node.getName().toLowerCase() + "." + entry.getDomainName();
			DnsRecordDetail recordDetail = api.createRecord(
				getDriver().getConfig().getIdentity(),
				entry,
				new DefaultDnsRecord(
					DnsType.A,
					endpoint,
					node.getAddress().getHost(),
					Document.empty()
				)
			);

			if (recordDetail != null) {
				getLogger().info("Created dns entry for node '{}' on domain '{}' (-> {})", node.getName(), entry.getDomainName(), endpoint);
				getLogger().extended("=> {}", recordDetail.getDnsRecord());
			}
		}
	}

	public void createCloudflareDnsServiceEntries(@Nonnull ServiceInfo service) {
		for (CloudflareConfigEntry entry : config.getEntries()) {
			if (!entry.isEnabled()) continue;

			for (CloudflareConfigEntryGroup group : entry.getGroups()) {

				if (!group.getTaskName().equalsIgnoreCase(service.getTaskName()))
					continue;

				DnsRecordDetail recordDetail = api.createRecord(
					service.getUniqueId(),
					entry,
					SrvRecord.forService(entry, group, service)
				);

				if (recordDetail != null) {
					getLogger().info("Created dns entry for service '{}' port:{} on domain '{}'", service.getName(), service.getPort(), entry.getDomainName());
					getLogger().extended("=> {}", recordDetail.getDnsRecord());
				}

			}
		}
	}

	@Nonnull
	public CloudflareConfig getCloudflareConfig() {
		return config;
	}

	@Nonnull
	public CloudflareAPI getCloudflareAPI() {
		return api;
	}

	public static CloudCloudflareModule getInstance() {
		return instance;
	}
}
