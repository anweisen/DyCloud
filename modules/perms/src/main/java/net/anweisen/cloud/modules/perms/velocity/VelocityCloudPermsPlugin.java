package net.anweisen.cloud.modules.perms.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.anweisen.cloud.modules.perms.velocity.listener.VelocityCloudPermsListener;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Plugin(id = "cloud-perms")
public final class VelocityCloudPermsPlugin {

	private final ProxyServer server;

	@Inject
	public VelocityCloudPermsPlugin(@Nonnull ProxyServer server) {
		this.server = server;
	}


	@Subscribe
	public void onProxyInitialization(@Nonnull ProxyInitializeEvent event) {
		server.getEventManager().register(this, new VelocityCloudPermsListener());
	}

}
