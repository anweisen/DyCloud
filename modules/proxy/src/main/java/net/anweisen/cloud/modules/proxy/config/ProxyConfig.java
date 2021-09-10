package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyConfig {

	private ProxyTabListConfig tablist;
	private ProxyMotdConfig motd;

	private ProxyConfig() {
	}

	public ProxyConfig(@Nonnull ProxyTabListConfig tablist, @Nonnull ProxyMotdConfig motd) {
		this.tablist = tablist;
		this.motd = motd;
	}

	@Nonnull
	public ProxyTabListConfig getTablist() {
		return tablist;
	}

	@Nonnull
	public ProxyMotdConfig getMotd() {
		return motd;
	}
}
