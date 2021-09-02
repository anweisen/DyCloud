package net.anweisen.cloud.modules.proxy;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.modules.proxy.config.ProxyConfig;
import net.anweisen.cloud.modules.proxy.config.ProxyTabListConfig;
import net.anweisen.cloud.modules.proxy.config.ProxyTabListEntryConfig;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudProxyModule extends CloudModule {

	private static CloudProxyModule instance;

	private ProxyConfig config;

	@Override
	protected void onLoad() {
		instance = this;

		loadConfig();
		if (!getEnabled(true)) return;
	}

	private void loadConfig() {
		config = getConfig().toInstanceOf(ProxyConfig.class);
		getLogger().debug("Loaded config {}", config);
		if (config == null)
			getConfig().set(config = new ProxyConfig(
				new ProxyTabListConfig(
					Collections.singletonList(new ProxyTabListEntryConfig(
						Arrays.asList(
							"§7                                                              ",
							"§e§lMinecraftCloud §8» §7Dockerize your network",
							"§8§l► §a{players.online} §7/ §c{players.max} §8§l┃ §7Server §8» §e{service} §8§l◄",
							"§7"
						),
						Arrays.asList(
							"§7",
							"§8§l┃ §bTwitter §8● §7@anweisenet §8§l┃",
							"§8§l┃ §9GitHub §8● §7github.com/anweisen §8§l┃",
							"§8§l┃ §cWebsite §8● §7anweisen.net §8§l┃",
							"§7"
						)
					)),
					1
				)
			)).save();
		getGlobalConfig().set("proxyConfig", config).update();
	}

	@Nonnull
	public ProxyConfig getProxyConfig() {
		return config;
	}

	public static CloudProxyModule getInstance() {
		return instance;
	}
}
