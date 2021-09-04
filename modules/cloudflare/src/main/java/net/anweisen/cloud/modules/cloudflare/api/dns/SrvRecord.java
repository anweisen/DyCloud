package net.anweisen.cloud.modules.cloudflare.api.dns;

import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntry;
import net.anweisen.cloud.modules.cloudflare.config.CloudflareConfigEntryGroup;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

public class SrvRecord extends DnsRecord {

	public SrvRecord(String name, String content, String service, String proto, String secondName, int priority, int weight, int port, String target) {
		super(
			DnsType.SRV.name(),
			name,
			content,
			1,
			false,
			Document.create()
				.set("service", service)
				.set("proto", proto)
				.set("name", secondName)
				.set("priority", priority)
				.set("weight", weight)
				.set("port", port)
				.set("target", target)
		);
	}

	@Nonnull
	public static SrvRecord forService(@Nonnull CloudflareConfigEntry entry, @Nonnull CloudflareConfigEntryGroup group, @Nonnull ServiceInfo service) {
		return new SrvRecord(
			String.format("_minecraft._tcp.%s", entry.getDomainName()),
			String.format(
				"SRV %s %s %s %s.%s.%s",
				group.getPriority(),
				group.getWeight(),
				service.getPort(),
				CloudMaster.getInstance().getConfig().getIdentity(),
				service.getNodeName(),
				entry.getDomainName()
			),
			"_minecraft",
			"_tcp",
			group.getSubDomainName().isEmpty() ? entry.getDomainName() : group.getSubDomainName(),
			group.getPriority(),
			group.getWeight(),
			service.getPort(),
			CloudMaster.getInstance().getConfig().getIdentity() + "." + service.getNodeName() +  "." + entry.getDomainName()
		);
	}
}
