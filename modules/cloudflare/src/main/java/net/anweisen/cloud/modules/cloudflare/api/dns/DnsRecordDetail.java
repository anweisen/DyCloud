package net.anweisen.cloud.modules.cloudflare.api.dns;

import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntry;

public class DnsRecordDetail {

	private final String id;
	private final DnsRecord dnsRecord;
	private final CloudflareConfigEntry configurationEntry;

	public DnsRecordDetail(String id, DnsRecord dnsRecord, CloudflareConfigEntry configurationEntry) {
		this.id = id;
		this.dnsRecord = dnsRecord;
		this.configurationEntry = configurationEntry;
	}

	public String getId() {
		return this.id;
	}

	public DnsRecord getDnsRecord() {
		return this.dnsRecord;
	}

	public CloudflareConfigEntry getConfigEntry() {
		return configurationEntry;
	}
}
