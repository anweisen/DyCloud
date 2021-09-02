package net.anweisen.cloud.modules.notify;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.modules.notify.config.NotifyConfig;
import net.anweisen.cloud.modules.notify.listener.ServiceStatusListener;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudNotifyModule extends CloudModule {

	private static CloudNotifyModule instance;

	private NotifyConfig config;

	@Override
	protected void onLoad() {
		instance = this;

		loadConfig();
		if (!getEnabled(true)) return;
		initListeners();
	}

	private void loadConfig() {
		config = getConfig().toInstanceOf(NotifyConfig.class);
		getLogger().debug("Loaded config {}", config);
		if (config == null)
			getConfig().set(config = new NotifyConfig(
				"§e{service} §7is being §astarted §7on §e{node}§7..",
				"§e{service} §7is being §cstarted §7on §e{node}§7..",
				"§e{service} §7was §cstopped §7on §e{node}§7..",
				"§8» §7Click to §e§lconnect"
			)).save();
	}

	private void initListeners() {
		registerListeners(new ServiceStatusListener());
	}

	@Nonnull
	public NotifyConfig getNotifyConfig() {
		return config;
	}

	public static CloudNotifyModule getInstance() {
		return instance;
	}
}
