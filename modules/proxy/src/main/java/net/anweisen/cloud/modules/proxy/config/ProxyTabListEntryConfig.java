package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyTabListEntryConfig {

	private Collection<String> header;
	private Collection<String> footer;

	private ProxyTabListEntryConfig() {
	}

	public ProxyTabListEntryConfig(@Nonnull Collection<String> header, @Nonnull Collection<String> footer) {
		this.header = header;
		this.footer = footer;
	}

	@Nonnull
	public Collection<String> getHeader() {
		return header;
	}

	@Nonnull
	public Collection<String> getFooter() {
		return footer;
	}
}
