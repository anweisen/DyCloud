package net.anweisen.cloud.modules.proxy.bungee.listener;

import net.anweisen.cloud.modules.proxy.bungee.BungeeCloudProxyPlugin;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudProxyListener implements Listener {

	@EventHandler
	public void onLogin(@Nonnull LoginEvent event) {
		BungeeCloudProxyPlugin.getInstance().getManager().updateTabList();
	}

	@EventHandler
	public void onPing(@Nonnull ProxyPingEvent event) {
		event.setResponse(BungeeCloudProxyPlugin.getInstance().getManager().getMotd(event.getResponse()));
	}

}
