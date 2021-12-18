package net.anweisen.cloud.modules.cloudflare.api.dns;

import net.anweisen.utility.document.Document;

import javax.annotation.Nonnull;

public class DefaultDnsRecord extends DnsRecord {

	public DefaultDnsRecord(@Nonnull DnsType type, @Nonnull String name, @Nonnull String content, @Nonnull Document data) {
		super(type.name(), name, content, 1, false, data);
	}

}
