package net.anweisen.cloud.modules.proxy.bungee;

import net.anweisen.cloud.modules.proxy.helper.AbstractCloudProxyManager;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudProxyManager extends AbstractCloudProxyManager {

	private final Plugin plugin;

	public BungeeCloudProxyManager(@Nonnull Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void updateTabList() {
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			player.setTabHeader(
				TextComponent.fromLegacyText(replace(header, player)),
				TextComponent.fromLegacyText(replace(footer, player))
			);
		}
	}

	@Nonnull
	public String replace(@Nonnull String content, @Nonnull ProxiedPlayer player) {
		return replaceDefault(content, player.getUniqueId())
			.replace("{service}", player.getServer() == null ? "N/A" : player.getServer().getInfo().getName())
			.replace("{proxy}", CloudWrapper.getInstance().getServiceInfo().getName())
			.replace("{node}", CloudWrapper.getInstance().getServiceInfo().getNodeName())
			.replace("{name}", player.getName())
			.replace("{ping}", String.valueOf(player.getPing()))
		;
	}

	@Override
	public void schedule(@Nonnull Runnable command, long millis) {
		ProxyServer.getInstance().getScheduler().schedule(plugin, command, 0, millis, TimeUnit.MILLISECONDS);
	}
}
