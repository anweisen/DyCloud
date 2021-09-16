package net.anweisen.cloud.modules.proxy.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import net.anweisen.cloud.modules.proxy.velocity.VelocityCloudProxyPlugin;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class VelocityCloudProxyListener {

	@Subscribe
	public void onLogin(@Nonnull LoginEvent event) {
		VelocityCloudProxyPlugin.getInstance().getManager().updateTabList();
	}

	@Subscribe
	public void onPing(@Nonnull ProxyPingEvent event) {
		event.setPing(VelocityCloudProxyPlugin.getInstance().getManager().getMotd(event.getPing()));
	}

}
