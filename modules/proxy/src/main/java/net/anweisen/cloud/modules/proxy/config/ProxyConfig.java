package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyConfig {

	private boolean useTranslations;
	private ProxyTabListConfig tablist;
	private ProxyMotdConfig motd;

	private ProxyConfig() {
	}

	public ProxyConfig(boolean useTranslations, @Nonnull ProxyTabListConfig tablist, @Nonnull ProxyMotdConfig motd) {
		this.useTranslations = useTranslations;
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

	public boolean isUseTranslations() {
		return useTranslations;
	}

	@Override
	public String toString() {
		return "ProxyConfig[" +
			"useTranslations=" + useTranslations +
			" tablist=" + tablist +
			" motd=" + motd +
			']';
	}
}
