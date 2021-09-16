package net.anweisen.cloud.modules.proxy.bungee;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.modules.proxy.config.ProxyMotdEntryConfig;
import net.anweisen.cloud.modules.proxy.helper.AbstractCloudProxyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudProxyManager extends AbstractCloudProxyManager {

	private final BungeeCloudProxyPlugin plugin;

	public BungeeCloudProxyManager(@Nonnull BungeeCloudProxyPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void updateTabList() {
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			player.setTabHeader(
				TextComponent.fromLegacyText(replaceBungeePlayer(header, player)),
				TextComponent.fromLegacyText(replaceBungeePlayer(footer, player))
			);
		}
	}

	@Nonnull
	public String replaceBungeePlayer(@Nonnull String content, @Nonnull ProxiedPlayer player) {
		return replacePlayer(content, player.getUniqueId())
			.replace("{service}", player.getServer() == null ? "N/A" : player.getServer().getInfo().getName())
			.replace("{name}", player.getName())
			.replace("{ping}", String.valueOf(player.getPing()))
		;
	}

	@Nonnull
	public ServerPing getMotd(@Nonnull ServerPing original) {
		CloudDriver cloud = CloudDriver.getInstance();
		ProxyMotdEntryConfig motd = getMotdEntry();
		if (motd == null) return original;

		String motdText = replaceDefault(motd.getFirstLine() + '\n' + motd.getSecondLine());
		String protocolText = motd.getProtocolText() == null ? null : replaceDefault(motd.getProtocolText());

		PlayerInfo[] playerInfo = new PlayerInfo[motd.getPlayerInfo() == null || motd.getPlayerInfo().isEmpty() ? 0 : motd.getPlayerInfo().size()];
		for (int i = 0; i < playerInfo.length; i++) {
			playerInfo[i] = new PlayerInfo(motd.getPlayerInfo().get(i), UUID.randomUUID());
		}

		return new ServerPing(
			new Protocol(protocolText == null ? original.getVersion().getName() : protocolText, protocolText == null ? original.getVersion().getProtocol() : 1),
			new Players(cloud.getGlobalConfig().getMaxPlayers(), cloud.getPlayerManager().getOnlinePlayerCount(), playerInfo),
			new TextComponent(TextComponent.fromLegacyText(motdText)),
			original.getFaviconObject()
		);
	}

	@Override
	public void schedule(@Nonnull Runnable command, long millis) {
		ProxyServer.getInstance().getScheduler().schedule(plugin, command, 0, millis, TimeUnit.MILLISECONDS);
	}
}
