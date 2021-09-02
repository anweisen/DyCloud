package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyConfig {

	private ProxyTabListConfig tablist;

	private ProxyConfig() {
	}

	public ProxyConfig(@Nonnull ProxyTabListConfig tablist) {
		this.tablist = tablist;
	}

	@Nonnull
	public ProxyTabListConfig getTablist() {
		return tablist;
	}

}
