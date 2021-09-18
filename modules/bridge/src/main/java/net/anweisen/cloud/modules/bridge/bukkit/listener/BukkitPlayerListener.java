package net.anweisen.cloud.modules.bridge.bukkit.listener;

import net.anweisen.cloud.modules.bridge.bukkit.BukkitCloudBridgePlugin;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeNetworkingHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitPlayerListener implements Listener {

	@EventHandler
	public void onLogin(@Nonnull PlayerLoginEvent event) {
		BridgeNetworkingHelper.sendServerLoginRequestPacket(event.getPlayer().getUniqueId());
		BridgeHelper.updateServiceInfo();
	}

	@EventHandler
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		BridgeNetworkingHelper.sendServerLoginSuccessPacket(event.getPlayer().getUniqueId());
		BridgeHelper.updateServiceInfo();
	}

	@EventHandler
	public void onQuit(@Nonnull PlayerQuitEvent event) {
		Bukkit.getScheduler().runTask(BukkitCloudBridgePlugin.getInstance(), () -> {
			BridgeNetworkingHelper.sendServerDisconnectPacket(event.getPlayer().getUniqueId());
			BridgeHelper.updateServiceInfo();
		});
	}

}
