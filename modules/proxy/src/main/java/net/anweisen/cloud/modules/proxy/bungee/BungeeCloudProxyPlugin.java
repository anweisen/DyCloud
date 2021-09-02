package net.anweisen.cloud.modules.proxy.bungee;

import net.anweisen.cloud.modules.proxy.bungee.listener.BungeeCloudProxyListener;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudProxyPlugin extends Plugin {

	private static BungeeCloudProxyPlugin instance;

	private BungeeCloudProxyManager manager;

	@Override
	public void onLoad() {
		instance = this;

		manager = new BungeeCloudProxyManager(this);
		manager.requestConfig();

		getProxy().getPluginManager().registerListener(this, new BungeeCloudProxyListener());
	}

	@Nonnull
	public BungeeCloudProxyManager getManager() {
		return manager;
	}

	public static BungeeCloudProxyPlugin getInstance() {
		return instance;
	}
}
