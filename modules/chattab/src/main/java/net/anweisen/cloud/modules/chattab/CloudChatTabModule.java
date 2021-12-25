package net.anweisen.cloud.modules.chattab;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.modules.chattab.config.ChatConfig;
import net.anweisen.cloud.modules.chattab.config.ChatTabConfig;
import net.anweisen.cloud.modules.chattab.config.TabConfig;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudChatTabModule extends CloudModule {

	private static CloudChatTabModule instance;

	private ChatTabConfig config;

	@Override
	protected void onLoad() {
		instance = this;

		loadConfig();
	}

	private void loadConfig() {
		config = getConfig().toInstance(ChatTabConfig.class);
		getLogger().debug("Loaded config {}", config);
		if (config == null) {
			getConfig().set(config = new ChatTabConfig(
				new TabConfig(true, "", ""),
				new ChatConfig(true, "{player.display} §8» §7{message}")
			));
			getConfig().save();
		}
		getGlobalConfig().set("chattabConfig", config).update();
	}

	@Override
	public boolean isEnabled() {
		return config.getChat().isEnabled() || config.getTablist().isEnabled();
	}

	public static CloudChatTabModule getInstance() {
		return instance;
	}
}
