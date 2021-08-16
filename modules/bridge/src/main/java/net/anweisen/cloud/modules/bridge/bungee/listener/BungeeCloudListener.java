package net.anweisen.cloud.modules.bridge.bungee.listener;

import net.anweisen.cloud.modules.bridge.bungee.BungeeBridgeHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.ServiceRegisteredEvent;
import net.anweisen.cloud.driver.event.service.ServiceUnregisteredEvent;
import net.anweisen.cloud.driver.service.specific.ServiceProperties;
import net.anweisen.cloud.modules.bridge.helper.data.PluginInfo;
import net.anweisen.cloud.modules.bridge.helper.data.ProxyPlayerInfo;
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
		event.getServiceInfo().getProperties()
			.set(ServiceProperties.MAX_PLAYER_COUNT, BridgeHelper.getMaxPlayers())
			.set(ServiceProperties.ONLINE_COUNT, ProxyServer.getInstance().getOnlineCount())
			.set(ServiceProperties.MESSAGE_CHANNELS, ProxyServer.getInstance().getChannels())
			.set(ServiceProperties.PLAYERS, ProxyServer.getInstance().getPlayers().stream().map(player -> {
				return new ProxyPlayerInfo(player.getName(), player.getUniqueId(), player.getServer() != null ? player.getServer().getInfo().getName() : null);
			}).collect(Collectors.toList()))
			.set(ServiceProperties.PLUGINS, ProxyServer.getInstance().getPluginManager().getPlugins().stream().map(Plugin::getDescription).map(plugin -> {
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
	public void onServiceUnregister(@Nonnull ServiceUnregisteredEvent event) {
		if (event.getServiceInfo().getEnvironment().isServer()) {
			BungeeBridgeHelper.unregisterServer(event.getServiceInfo().getName());
		}
	}

}
