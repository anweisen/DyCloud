package net.anweisen.cloud.modules.perms.bungee;

import net.anweisen.cloud.modules.perms.bungee.listener.BungeeCloudPermsListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BungeeCloudPermsPlugin extends Plugin {

	@Override
	public void onEnable() {
		initListeners();
	}

	private void initListeners() {
		ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeCloudPermsListener());
	}

}
