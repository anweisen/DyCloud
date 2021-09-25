package net.anweisen.cloud.modules.bridge.bukkit.listener;

import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.service.specific.ServiceProperty;
import net.anweisen.cloud.driver.service.specific.data.PlayerInfo;
import net.anweisen.cloud.driver.service.specific.data.PluginInfo;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.wrapper.event.service.ServiceInfoConfigureEvent;
import net.anweisen.utilities.common.misc.SimpleCollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitCloudListener {

	@EventListener
	public void onInfoConfigure(@Nonnull ServiceInfoConfigureEvent event) {
		BridgeHelper.setOnlineCount(Bukkit.getOnlinePlayers().size());
		event.getServiceInfo().setReady();
		event.getServiceInfo()
			.set(ServiceProperty.WHITELIST, Bukkit.hasWhitelist())
			.set(ServiceProperty.MOTD, BridgeHelper.getMotd())
			.set(ServiceProperty.STATUS, BridgeHelper.getStatus())
			.set(ServiceProperty.EXTRA, BridgeHelper.getExtra())
			.set(ServiceProperty.MAX_PLAYERS, BridgeHelper.getMaxPlayers())
			.set(ServiceProperty.ONLINE_PLAYERS, Bukkit.getOnlinePlayers().size())
			.set(ServiceProperty.MESSAGING_CHANNELS, SimpleCollectionUtils.setOf(Bukkit.getMessenger().getIncomingChannels(), Bukkit.getMessenger().getOutgoingChannels()))
			.set(ServiceProperty.PLAYERS, Bukkit.getOnlinePlayers().stream().map(player -> {
				return new PlayerInfo(player.getUniqueId(), player.getName());
			}).collect(Collectors.toList()))
			.set(ServiceProperty.PLUGINS, Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getDescription).map(plugin -> {
				return new PluginInfo(plugin.getName(), plugin.getAuthors().toArray(new String[0]), plugin.getVersion(), plugin.getMain(), plugin.getDescription());
			}).collect(Collectors.toList()));
	}

}
