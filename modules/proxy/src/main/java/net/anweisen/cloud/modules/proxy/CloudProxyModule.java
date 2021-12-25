package net.anweisen.cloud.modules.proxy;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.modules.proxy.config.*;

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
		if (!checkEnabled(true)) return;
	}

	private void loadConfig() {
		config = getConfig().toInstance(ProxyConfig.class);
		getLogger().debug("Loaded config {}", config);
		if (config == null) {
			getConfig().set(config = new ProxyConfig(
				false,
				new ProxyTabListConfig(
					Collections.singletonList(new ProxyTabListEntryConfig(
						String.join("\n", Arrays.asList(
							"§7                                                              ",
							"§8〢 §e§lDyCloud §8⏹ §7Dockerize your network",
							"§8► §7Online §8» §a{players.online} §7/ §c{players.max} §8§l┃ §7Server §8» §e{service} §8◄",
							"§7"
						)),
						String.join("\n", Arrays.asList(
							"§7",
							"§8§l┃ §bTwitter §8● §7@anweisenet §8§l┃",
							"§8§l┃ §9GitHub §8● §7github.com/anweisen §8§l┃",
							"§8§l┃ §cWebsite §8● §7anweisen.net §8§l┃",
							"§7"
						))
					)),
					1
				),
				new ProxyMotdConfig(
					Collections.singletonList(
						new ProxyMotdEntryConfig(
							"§e§lDyCloud §8● §7Dockerize your network",
							"§7Status §8» §aOnline §8§l┃ §7Version §8» §e1§7.§68§7.§e§k? §7- §e1§7.§617§7.§e§k?",
							null,
							Collections.emptyList()
						)
					),
					Collections.singletonList(
						new ProxyMotdEntryConfig(
							"§e§lDyCloud §8● §7Dockerize your network",
							"§7Status §8» §cMaintenance §8§l┃ §7Website §8» §eanweisen§7.§enet",
							"§4✖ §8┃ §cMaintenance",
							Collections.emptyList()
						)
					)
				)
			));
			getConfig().save();
		}
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
