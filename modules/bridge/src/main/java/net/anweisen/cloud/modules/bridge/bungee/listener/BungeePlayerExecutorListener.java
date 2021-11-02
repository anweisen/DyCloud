package net.anweisen.cloud.modules.bridge.bungee.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerExecutorPacket.PlayerExecutorPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.player.defaults.DefaultPlayerExecutor;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.cloud.modules.bridge.bungee.BungeeBridgeHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeePlayerExecutorListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		PacketBuffer buffer = packet.getBuffer();

		PlayerExecutorPayload payload = buffer.readEnum(PlayerExecutorPayload.class);
		UUID playerUniqueId = buffer.readUniqueId();
		boolean global = playerUniqueId.equals(DefaultPlayerExecutor.GLOBAL_UUID);
		ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(playerUniqueId);
		if (global && targetPlayer == null) return;
		Collection<ProxiedPlayer> players = global ? ProxyServer.getInstance().getPlayers() : Collections.singletonList(targetPlayer);

		debug("PlayerExecutorPayload.{} -> {}", payload, players);
		switch (payload) {
			case SEND_MESSAGE: {
				String permission = buffer.readOptionalString();
				ChatText[] messages = buffer.readObjectArray(ChatText.class);

				BaseComponent[] components = BungeeBridgeHelper.buildChatTextComponents(messages);
				for (ProxiedPlayer player : players) {
					if (permission != null && !player.hasPermission(permission)) break;
					player.sendMessage(components);
				}
				break;
			}
			case SEND_TRANSLATION: {
				String name = buffer.readString();
				String[] args = buffer.readStringArray();

				Translatable translatable = Translatable.of(name);
				for (ProxiedPlayer player : players) {
					ChatText[] messages = translatable.translate(player.getUniqueId()).asText((Object[]) args);
					BaseComponent[] components = BungeeBridgeHelper.buildChatTextComponents(messages);
					player.sendMessage(components);
				}
				break;
			}
			case SEND_ACTIONBAR: {
				String actionbar = buffer.readString();
				BaseComponent[] components = TextComponent.fromLegacyText(actionbar);
				for (ProxiedPlayer player : players) {
					player.sendMessage(ChatMessageType.ACTION_BAR, components);
				}
				break;
			}
			case SEND_TITLE: {
				Title title = ProxyServer.getInstance().createTitle()
					.title(TextComponent.fromLegacyText(buffer.readString()))
					.subTitle(TextComponent.fromLegacyText(buffer.readString()))
					.fadeIn(buffer.readVarInt())
					.stay(buffer.readVarInt())
					.fadeOut(buffer.readVarInt());
				for (ProxiedPlayer player : players) {
					title.send(player);
				}
				break;
			}
			case CONNECT_SERVER: {
				String serverName = buffer.readString();
				ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
				for (ProxiedPlayer player : players) {
					player.connect(serverInfo);
				}
				break;
			}
			case CONNECT_FALLBACK: {
				for (ProxiedPlayer player : players) {
					ServerInfo fallback = BungeeBridgeHelper.getNextFallback(player);
					player.connect(fallback);
				}
				break;
			}
			case DISCONNECT: {
				String reason = buffer.readString();
				BaseComponent[] components = TextComponent.fromLegacyText(reason);
				for (ProxiedPlayer player : players) {
					player.disconnect(components);
				}
				break;
			}
		}
	}

}
