package net.anweisen.cloud.modules.bridge.bungee;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.bungee.listener.BungeeCloudListener;
import net.anweisen.cloud.modules.bridge.bungee.listener.BungeePlayerExecutorListener;
import net.anweisen.cloud.modules.bridge.bungee.listener.BungeePlayerListener;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeProxyHelper;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BungeeCloudBridgePlugin extends Plugin implements LoggingApiUser {

	private static BungeeCloudBridgePlugin instance;

	@Override
	public void onEnable() {
		instance = this;

		initListeners();
		initHelpers();
		initServers();
		initCommands();

		getProxy().setReconnectHandler(new BungeeCloudReconnectHandler());
		getProxy().getScheduler().schedule(this, BridgeProxyHelper::checkPlayerDisconnects, 3, 3, TimeUnit.SECONDS);
	}

	@Override
	public void onDisable() {
		CloudWrapper.getInstance().getEventManager().unregisterListener(this.getClass().getClassLoader());
	}

	private void initHelpers() {
		BridgeHelper.setMaxPlayers(this.getProxy().getConfig().getPlayerLimit());
		BridgeHelper.setMotd(new ArrayList<>(this.getProxy().getConfig().getListeners()).get(0).getMotd());
		BridgeHelper.setPhase("LOBBY");
		BridgeHelper.updateServiceInfo();
		BridgeProxyHelper.setMethods(BungeeBridgeHelper.methods());
	}

	private void initListeners() {
		CloudWrapper.getInstance().getEventManager().registerListeners(new BungeeCloudListener());
		CloudWrapper.getInstance().getSocketComponent().getListenerRegistry().addListener(PacketConstants.PLAYER_EXECUTOR_CHANNEL, new BungeePlayerExecutorListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeePlayerListener());
	}

	private void initServers() {
		debug("Registering services in proxy registry..");
		for (ServiceInfo service : CloudWrapper.getInstance().getServiceManager().getServiceInfos()) {
			trace("- {} ", service.toShortString());
			if (!service.getEnvironment().isServer())
				continue;

			BungeeBridgeHelper.registerServer(service);
		}
	}

	private void initCommands() {
		BridgeProxyHelper.updateCommands();
	}

	public static BungeeCloudBridgePlugin getInstance() {
		return instance;
	}
}
