package net.anweisen.cloud.modules.bridge.bungee;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.global.objects.CommandObject;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.player.connection.DefaultPlayerConnection;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.settings.*;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.bungee.command.BungeeCommand;
import net.anweisen.cloud.modules.bridge.helper.BridgeNetworkingHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeProxyHelper;
import net.anweisen.cloud.modules.bridge.helper.BridgeProxyMethods;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BungeeBridgeHelper {

	private BungeeBridgeHelper() {}

	@Nonnull
	public static PlayerConnection createPlayerConnection(@Nonnull PendingConnection connection) {
		return new DefaultPlayerConnection(
			CloudWrapper.getInstance().getServiceInfo().getName(),
			HostAndPort.fromSocketAddress(connection.getSocketAddress()),
			connection.getVersion(),
			connection.isOnlineMode(),
			connection.isLegacy()
		);
	}

	@Nonnull
	public static PlayerSettings createPlayerSettings(@Nonnull ProxiedPlayer player) {
		return new DefaultPlayerSettings(
			player.getLocale(),
			player.getViewDistance(),
			player.hasChatColors(),
			new DefaultSkinParts(
				player.getSkinParts().hasCape(),
				player.getSkinParts().hasJacket(),
				player.getSkinParts().hasLeftSleeve(),
				player.getSkinParts().hasRightSleeve(),
				player.getSkinParts().hasLeftPants(),
				player.getSkinParts().hasRightPants(),
				player.getSkinParts().hasHat()
			),
			ChatMode.valueOf(player.getChatMode().name()),
			MainHand.valueOf(player.getMainHand().name())
		);
	}

	@Nullable
	public static ServerInfo getNextFallback(@Nonnull ProxiedPlayer player) {
		ServiceInfo service = BridgeProxyHelper.getNextFallback(player.getUniqueId(), player::hasPermission);
		if (service == null) return null;

		ServerInfo server = ProxyServer.getInstance().getServerInfo(service.getName());
		if (server == null) {
			CloudDriver.getInstance().getLogger().warn("Server {} is not registered in the proxy", service);
			for (ServerInfo current : ProxyServer.getInstance().getServers().values()) {
				CloudDriver.getInstance().getLogger().extended("=> {}: {}", current.getName(), current.getSocketAddress());
			}
			registerServer(service);
			server = ProxyServer.getInstance().getServerInfo(service.getName());
		}

		return server;
	}

	public static void registerServer(@Nonnull ServiceInfo serviceInfo) {
		CloudWrapper.getInstance().getLogger().debug("Registering server '{}' -> {}", serviceInfo.getName(), serviceInfo.getAddress());
		ProxyServer.getInstance().getServers().put(serviceInfo.getName(), ProxyServer.getInstance().constructServerInfo(
			serviceInfo.getName(),
			serviceInfo.getAddress().toSocketAddress(), "A DyCloud service", false
		));
	}

	public static void unregisterServer(@Nonnull String name) {
		CloudWrapper.getInstance().getLogger().debug("Unregistering server '{}'", name);
		ProxyServer.getInstance().getServers().remove(name);
	}

	public static void updateCommands(@Nonnull Map<String, Collection<CommandObject>> mapping) {
		ProxyServer.getInstance().getPluginManager().unregisterCommands(BungeeCloudBridgePlugin.getInstance());
		mapping.forEach((name, commands) -> {
			CloudDriver.getInstance().getLogger().trace("- '{}' | {}", name, commands);
			ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCloudBridgePlugin.getInstance(), new BungeeCommand(name, commands));
		});
	}

	public static void checkPlayerDisconnect(@Nonnull CloudPlayer player) {
		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUniqueId());
		if (proxiedPlayer == null) {
			BridgeNetworkingHelper.sendProxyRemovePacket(player.getUniqueId());
			CloudDriver.getInstance().getPlayerManager().unregisterOnlinePlayer(player.getUniqueId());
		}
	}

	@Nonnull
	@SuppressWarnings("deprecation")
	public static BaseComponent[] buildChatTextComponents(@Nonnull ChatText[] messages) {
		List<BaseComponent> components = new ArrayList<>(messages.length);
		ChatColor lastColor = ChatColor.WHITE;
		for (ChatText message : messages) {

			BaseComponent[] current = TextComponent.fromLegacyText(message.getText(), lastColor);
			for (BaseComponent child : current) {
				if (message.getClickReaction() != null)
					child.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(message.getClickReaction().toString()), message.getClick()));
				if (message.getHover() != null)
					child.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(message.getHover())));

				components.add(child);

				if (child.getColorRaw() != null) {
					lastColor = child.getColorRaw();
				} else  {
					child.setColor(lastColor);
				}
			}
		}

		return components.toArray(new BaseComponent[0]);
	}

	@Nonnull
	public static BridgeProxyMethods methods() {
		return new BridgeProxyMethods() {

			@Override
			public void updateCommands(@Nonnull Map<String, Collection<CommandObject>> mapping) {
				BungeeBridgeHelper.updateCommands(mapping);
			}

			@Override
			public void registerServer(@Nonnull ServiceInfo service) {
				BungeeBridgeHelper.registerServer(service);
			}

			@Override
			public void unregisterServer(@Nonnull String name) {
				BungeeBridgeHelper.unregisterServer(name);
			}

			@Override
			public void checkPlayerDisconnect(@Nonnull CloudPlayer player) {
				BungeeBridgeHelper.checkPlayerDisconnect(player);
			}
		};
	}

}
