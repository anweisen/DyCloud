package net.anweisen.cloud.modules.bridge.bungee.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.bungee.BungeeBridgeHelper;
import net.anweisen.cloud.modules.bridge.bungee.BungeeCloudBridgePlugin;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.utilities.common.collection.pair.Tuple;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeePlayerListener implements Listener, LoggingApiUser {

	@EventHandler
	public void onLogin(@Nonnull LoginEvent event) {
		Tuple<CloudPlayer, String> response = BridgeHelper.sendProxyLoginRequestPacket(BungeeBridgeHelper.createPlayerConnection(event.getConnection()));

		CloudPlayer player = response.getFirst();
		CloudDriver.getInstance().getPlayerManager().registerPlayer(player);

		String kickReason = response.getSecond();
		if (kickReason != null) {
			event.setCancelled(true);
			event.setCancelReason(TextComponent.fromLegacyText(kickReason));
		}
	}

	@EventHandler
	public void onServerConnectRequest(@Nonnull ServerConnectEvent event) {

		if (event.getReason() == Reason.JOIN_PROXY) {
			BridgeHelper.sendProxyLoginSuccessPacket(BungeeBridgeHelper.createPlayerConnection(event.getPlayer().getPendingConnection()));
			BridgeHelper.updateServiceInfo();
		}

		ServiceInfo serviceInfo = BridgeHelper.getCachedService(event.getTarget().getName());
		if (serviceInfo != null) {
			BridgeHelper.sendProxyServerConnectRequestPacket(BungeeBridgeHelper.createPlayerConnection(event.getPlayer().getPendingConnection()), serviceInfo);
		}

	}

	@EventHandler
	public void onServerSwitch(@Nonnull ServerSwitchEvent event) {

		if (event.getFrom() == null) return;
		ServiceInfo from = BridgeHelper.getCachedService(event.getFrom().getName());
		ServiceInfo to = BridgeHelper.getCachedService(event.getPlayer().getServer().getInfo().getName());
		if (from != null && to != null) {
			BridgeHelper.sendProxyServerSwitchPacket(BungeeBridgeHelper.createPlayerConnection(event.getPlayer().getPendingConnection()), from, to);
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

			BridgeHelper.getOrCreateFallbackHistory(player.getUniqueId()).addFailedConnection(kickedFrom.getName());
			ServerInfo fallback = BungeeBridgeHelper.getNextFallback(player);
			if (fallback != null) {
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
			BridgeHelper.sendProxyDisconnectPacket(BungeeBridgeHelper.createPlayerConnection(event.getPlayer().getPendingConnection()));
			BridgeHelper.removeFallbackHistory(event.getPlayer().getUniqueId());
		}, 50, TimeUnit.MILLISECONDS);
	}

}
