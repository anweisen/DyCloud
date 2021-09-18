package net.anweisen.cloud.modules.bridge.bungee.listener;

import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.ServiceReadyEvent;
import net.anweisen.cloud.driver.event.service.ServiceRegisteredEvent;
import net.anweisen.cloud.driver.event.service.ServiceStartedEvent;
import net.anweisen.cloud.driver.event.service.ServiceUnregisteredEvent;
import net.anweisen.cloud.driver.service.specific.ServiceProperty;
import net.anweisen.cloud.driver.service.specific.data.PlayerInfo;
import net.anweisen.cloud.driver.service.specific.data.PluginInfo;
import net.anweisen.cloud.modules.bridge.bungee.BungeeBridgeHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.wrapper.event.service.ServiceInfoConfigureEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudListener {

	@EventListener
	public void onInfoConfigure(@Nonnull ServiceInfoConfigureEvent event) {
		BridgeHelper.setOnlineCount(ProxyServer.getInstance().getOnlineCount());
		event.getServiceInfo().setReady();
		event.getServiceInfo()
			.set(ServiceProperty.MOTD, BridgeHelper.getMotd())
			.set(ServiceProperty.STATUS, BridgeHelper.getStatus())
			.set(ServiceProperty.EXTRA, BridgeHelper.getExtra())
			.set(ServiceProperty.MAX_PLAYERS, BridgeHelper.getMaxPlayers())
			.set(ServiceProperty.ONLINE_PLAYERS, ProxyServer.getInstance().getOnlineCount())
			.set(ServiceProperty.MESSAGING_CHANNELS, ProxyServer.getInstance().getChannels())
			.set(ServiceProperty.PLAYERS, ProxyServer.getInstance().getPlayers().stream().map(player -> {
				return new PlayerInfo(player.getUniqueId(), player.getName());
			}).collect(Collectors.toList()))
			.set(ServiceProperty.PLUGINS, ProxyServer.getInstance().getPluginManager().getPlugins().stream().map(Plugin::getDescription).map(plugin -> {
				return new PluginInfo(plugin.getName(), new String[] { plugin.getAuthor() }, plugin.getVersion(), plugin.getMain(), plugin.getDescription());
			}).collect(Collectors.toList()))
		;
	}

	@EventListener
	public void onServiceRegister(@Nonnull ServiceRegisteredEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BungeeBridgeHelper.registerServer(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceStarted(@Nonnull ServiceStartedEvent event) {
		// Sometimes the servers are not registered in the proxy, so register the server again to be safe
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BungeeBridgeHelper.registerServer(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceReady(@Nonnull ServiceReadyEvent event) {
		// Sometimes the servers are not registered in the proxy, so register the server again to be safe
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BungeeBridgeHelper.registerServer(event.getServiceInfo());
		}
	}

	@EventListener
	public void onServiceUnregister(@Nonnull ServiceUnregisteredEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BungeeBridgeHelper.unregisterServer(event.getServiceInfo().getName());
		}
	}

}
