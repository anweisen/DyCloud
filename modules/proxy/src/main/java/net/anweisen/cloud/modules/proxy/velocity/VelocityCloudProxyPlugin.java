package net.anweisen.cloud.modules.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.anweisen.cloud.modules.proxy.velocity.listener.VelocityCloudProxyListener;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Plugin(id = "cloud-proxy")
public class VelocityCloudProxyPlugin {

	private static VelocityCloudProxyPlugin instance;

	private final ProxyServer server;
	private final Logger logger;

	private VelocityCloudProxyManager manager;

	@Inject
	public VelocityCloudProxyPlugin(@Nonnull ProxyServer server, @Nonnull Logger logger) {
		this.server = server;
		this.logger = logger;
	}

	@Subscribe
	public void onProxyInitialization(@Nonnull ProxyInitializeEvent event) {
		instance = this;

		manager = new VelocityCloudProxyManager(this);
		manager.init();

		server.getEventManager().register(this, new VelocityCloudProxyListener());
	}

	@Nonnull
	public ProxyServer getServer() {
		return server;
	}

	@Nonnull
	public VelocityCloudProxyManager getManager() {
		return manager;
	}

	public static VelocityCloudProxyPlugin getInstance() {
		return instance;
	}
}
