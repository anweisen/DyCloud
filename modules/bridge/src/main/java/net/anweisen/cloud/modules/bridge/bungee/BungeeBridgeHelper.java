package net.anweisen.cloud.modules.bridge.bungee;

import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.player.connection.DefaultPlayerConnection;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.settings.ChatMode;
import net.anweisen.cloud.driver.player.settings.DefaultPlayerSettings;
import net.anweisen.cloud.driver.player.settings.DefaultSkinParts;
import net.anweisen.cloud.driver.player.settings.MainHand;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BungeeBridgeHelper {

	private BungeeBridgeHelper() {}

	@Nonnull
	public static PlayerConnection createPlayerConnection(@Nonnull PendingConnection connection) {
		return new DefaultPlayerConnection(
			BridgeHelper.getServiceInfo().getName(),
			HostAndPort.fromSocketAddress(connection.getSocketAddress()),
			connection.getVersion(),
			connection.isOnlineMode(),
			connection.isLegacy()
		);
	}

	@Nonnull
	public static DefaultPlayerSettings createPlayerSettings(@Nonnull ProxiedPlayer player) {
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
		ServiceInfo service = BridgeHelper.getNextFallback(player.getUniqueId(), player::hasPermission);
		return service == null ? null : ProxyServer.getInstance().getServerInfo(service.getName()); // TODO warn if ServerInfo is null (should never happen, but if it does we want to know)
	}

	public static void registerServer(@Nonnull ServiceInfo serviceInfo) {
		CloudWrapper.getInstance().getLogger().debug("Registering server '{}' -> {}", serviceInfo.getName(), serviceInfo.getAddress());
		ProxyServer.getInstance().getServers().put(serviceInfo.getName(), ProxyServer.getInstance().constructServerInfo(
			serviceInfo.getName(),
			serviceInfo.getAddress().toSocketAddress(), "A MinecraftCloud service", false
		));
	}

	public static void unregisterServer(@Nonnull String name) {
		CloudWrapper.getInstance().getLogger().debug("Unregistering server '{}'", name);
		ProxyServer.getInstance().getServers().remove(name);
	}

	public static BaseComponent[] buildChatTextComponents(@Nonnull ChatText[] messages) {
		List<BaseComponent> components = new ArrayList<>(messages.length);
		ChatColor lastColor = ChatColor.WHITE;
		for (ChatText message : messages) {

			BaseComponent[] component = TextComponent.fromLegacyText(message.getText(), lastColor);
			for (BaseComponent current : component) {
				if (message.getClickEvent() != null)
					current.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(message.getClickEvent().toString()), message.getClick()));
				if (message.getHover() != null)
					current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(message.getHover())));
			}

			if (component.length > 0)
				lastColor = component[component.length - 1].getColor();

			components.addAll(Arrays.asList(component));
		}

		return components.toArray(new BaseComponent[0]);
	}

}
