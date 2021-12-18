package net.anweisen.cloud.modules.bridge.bungee.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.event.player.PlayerProxyLoginRequestEvent;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.cloud.modules.bridge.bungee.BungeeBridgeHelper;
import net.anweisen.cloud.modules.bridge.bungee.BungeeCloudBridgePlugin;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeNetworkingHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeProxyHelper;
import net.anweisen.utility.common.collection.pair.Tuple;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import static net.anweisen.cloud.modules.bridge.bungee.BungeeBridgeHelper.createPlayerConnection;
import static net.anweisen.cloud.modules.bridge.bungee.BungeeBridgeHelper.createPlayerSettings;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeePlayerListener implements Listener, LoggingApiUser {

	@EventHandler
	public void onLogin(@Nonnull LoginEvent event) {
		Tuple<CloudPlayer, String> response = BridgeNetworkingHelper.sendProxyLoginRequestPacket(event.getConnection().getUniqueId(), event.getConnection().getName(), createPlayerConnection(event.getConnection()));

		String kickReason = response.getSecond();
		if (kickReason != null) {
			event.setCancelled(true);
			event.setCancelReason(TextComponent.fromLegacyText(kickReason));
			return;
		}

		CloudPlayer player = response.getFirst();
		CloudDriver.getInstance().getPlayerManager().registerOnlinePlayer(player);
		CloudDriver.getInstance().getEventManager().callEvent(new PlayerProxyLoginRequestEvent(player));
	}

	@EventHandler
	public void onPostLogin(@Nonnull PostLoginEvent event) {
		BridgeNetworkingHelper.sendProxyLoginSuccessPacket(event.getPlayer().getUniqueId(), createPlayerSettings(event.getPlayer()));
		BridgeHelper.updateServiceInfo();
	}

	@EventHandler
	public void onServerConnectRequest(@Nonnull ServerConnectEvent event) {

		ServiceInfo serviceInfo = CloudDriver.getInstance().getServiceManager().getServiceInfoByName(event.getTarget().getName());
		if (serviceInfo != null) {
			BridgeNetworkingHelper.sendProxyServerConnectRequestPacket(event.getPlayer().getUniqueId(), serviceInfo.getUniqueId());
		}

	}

	@EventHandler
	public void onServerSwitch(@Nonnull ServerSwitchEvent event) {

		BridgeProxyHelper.clearFallbackHistory(event.getPlayer().getUniqueId());

		if (event.getFrom() == null) return;
		ServiceInfo from = CloudDriver.getInstance().getServiceManager().getServiceInfoByName(event.getFrom().getName());
		ServiceInfo to = CloudDriver.getInstance().getServiceManager().getServiceInfoByName(event.getPlayer().getServer().getInfo().getName());
		if (from != null && to != null) {
			BridgeNetworkingHelper.sendProxyServerSwitchPacket(event.getPlayer().getUniqueId(), from.getUniqueId(), to.getUniqueId());
		}

	}

	@EventHandler
	public void onKick(@Nonnull ServerKickEvent event) {
		ProxiedPlayer player = event.getPlayer();
		if (player.isConnected()) {
			ServerInfo kickedFrom = event.getKickedFrom();

			// TODO necessary?
			if (kickedFrom == null) {
				player.disconnect(event.getKickReasonComponent());
				event.setCancelled(true);
				return;
			}

			BridgeProxyHelper.getOrCreateFallbackHistory(player.getUniqueId()).addFailedConnection(kickedFrom.getName());
			ServerInfo fallback = BungeeBridgeHelper.getNextFallback(player);
			if (fallback == null) {
				player.disconnect(BungeeBridgeHelper.buildChatTextComponents(
					Translatable.of("cloud.kick.fallback.none").translate(player.getUniqueId()).asText()
				));
			} else {
				event.setCancelled(true);
				event.setCancelServer(fallback);
				player.sendMessage(event.getKickReasonComponent());
			}
		}
	}

	@EventHandler
	public void onDisconnect(@Nonnull PlayerDisconnectEvent event) {
		ProxyServer.getInstance().getScheduler().schedule(BungeeCloudBridgePlugin.getInstance(), () -> {
			BridgeHelper.updateServiceInfo();
			BridgeNetworkingHelper.sendProxyDisconnectPacket(event.getPlayer().getUniqueId());
			BridgeProxyHelper.clearFallbackHistory(event.getPlayer().getUniqueId());
		}, 50, TimeUnit.MILLISECONDS);
	}

	@EventHandler
	public void onSettingsChange(@Nonnull SettingsChangedEvent event) {
		PlayerSettings settings = createPlayerSettings(event.getPlayer());
		CloudPlayer player = CloudDriver.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(event.getPlayer().getUniqueId());
		if (!player.getSettings().equals(settings)) {
			BridgeNetworkingHelper.sendPlayerSettingsChangePacket(event.getPlayer().getUniqueId(), settings);
		}
	}

}
