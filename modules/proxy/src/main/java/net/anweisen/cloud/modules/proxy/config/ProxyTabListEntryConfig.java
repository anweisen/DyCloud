package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyTabListEntryConfig {

	private String header;
	private String footer;

	private ProxyTabListEntryConfig() {
	}

	public ProxyTabListEntryConfig(@Nonnull String header, @Nonnull String footer) {
		this.header = header;
		this.footer = footer;
	}

	@Nonnull
	public String getHeader() {
		return header;
	}

	@Nonnull
	public String getFooter() {
		return footer;
	}
}
