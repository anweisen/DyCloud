package net.anweisen.cloud.modules.cloudflare.config;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudflareConfig {

	private Collection<CloudflareConfigEntry> entries;

	private CloudflareConfig() {
	}

	public CloudflareConfig(@Nonnull Collection<CloudflareConfigEntry> entries) {
		this.entries = entries;
	}

	@Nonnull
	public Collection<CloudflareConfigEntry> getEntries() {
		return entries;
	}

	@Override
	public String toString() {
		return "CloudflareConfig" + entries;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CloudflareConfig that = (CloudflareConfig) o;
		return Objects.equals(entries, that.entries);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entries);
	}
}
