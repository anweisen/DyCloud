package net.anweisen.cloud.modules.bridge.bungee.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerExecutorPacket.PlayerExecutorType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeePlayerExecutorListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		Buffer buffer = packet.getBuffer();

		PlayerExecutorType type = buffer.readEnumConstant(PlayerExecutorType.class);
		UUID playerUniqueId = buffer.readUUID();
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerUniqueId);

		debug("{} -> '{}'", type, player);

		switch (type) {
			case SEND_MESSAGE: {
				String permission = buffer.readOptionalString();
				String[] messages = buffer.readStringArray();

				if (permission != null && !player.hasPermission(permission)) break;
				for (String message : messages) {
					player.sendMessage(TextComponent.fromLegacyText(message));
				}
				break;
			}
			case SEND_ACTIONBAR: {
				String actionbar = buffer.readString();
				player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
				break;
			}
			case SEND_TITLE: {
				ProxyServer.getInstance().createTitle()
					.title(TextComponent.fromLegacyText(buffer.readString()))
					.subTitle(TextComponent.fromLegacyText(buffer.readString()))
					.fadeIn(buffer.readVarInt())
					.stay(buffer.readVarInt())
					.fadeOut(buffer.readVarInt())
					.send(player);
				break;
			}
			case CONNECT_SERVER: {
				String serverName = buffer.readString();
				ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
				player.connect(serverInfo);
				break;
			}
			case DISCONNECT: {
				String reason = buffer.readString();
				player.disconnect(TextComponent.fromLegacyText(reason));
				break;
			}
		}
	}

}
