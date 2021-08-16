package net.anweisen.cloud.modules.bridge.bungee;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.bungee.listener.BungeeCloudListener;
import net.anweisen.cloud.modules.bridge.bungee.listener.BungeePlayerListener;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.modules.bridge.helper.general.BridgeCloudListener;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudBridgePlugin extends Plugin implements LoggingApiUser {

	private static BungeeCloudBridgePlugin instance;

	@Override
	public void onEnable() {
		instance = this;

		initListeners();
		initHelpers();
		initServers();

		getProxy().setReconnectHandler(new BungeeCloudReconnectHandler());
	}

	@Override
	public void onDisable() {
		CloudWrapper.getInstance().getEventManager().unregisterListener(this.getClass().getClassLoader());
	}

	private void initHelpers() {
		BridgeHelper.setMaxPlayers(this.getProxy().getConfig().getPlayerLimit());
		BridgeHelper.updateServiceInfo();
	}

	private void initListeners() {
		CloudWrapper.getInstance().getEventManager().registerListeners(new BridgeCloudListener(), new BungeeCloudListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeePlayerListener());
	}

	private void initServers() {
		debug("Registering services in proxy registry..");
		for (ServiceInfo serviceInfo : CloudWrapper.getInstance().getServiceManager().getServiceInfos()) {
			trace("Found {} ({}) -> {}:{}", serviceInfo.getName(), serviceInfo.getEnvironment(), serviceInfo.getState(), serviceInfo.isReady() ? "ready" : "unready");
			if (!serviceInfo.getEnvironment().isServer())
				continue;

			BungeeBridgeHelper.registerServer(serviceInfo);
		}
	}

	public static BungeeCloudBridgePlugin getInstance() {
		return instance;
	}
}
