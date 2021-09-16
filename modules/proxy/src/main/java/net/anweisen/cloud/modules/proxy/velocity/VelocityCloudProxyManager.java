package net.anweisen.cloud.modules.proxy.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.proxy.server.ServerPing.Players;
import com.velocitypowered.api.proxy.server.ServerPing.SamplePlayer;
import com.velocitypowered.api.proxy.server.ServerPing.Version;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.modules.proxy.config.ProxyMotdEntryConfig;
import net.anweisen.cloud.modules.proxy.helper.AbstractCloudProxyManager;
import net.kyori.text.TextComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class VelocityCloudProxyManager extends AbstractCloudProxyManager {

	private final VelocityCloudProxyPlugin plugin;

	public VelocityCloudProxyManager(VelocityCloudProxyPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void updateTabList() {
		for (Player player : plugin.getServer().getAllPlayers()) {
			player.getTabList().setHeaderAndFooter(
				TextComponent.of(replaceVelocityPlayer(header, player)),
				TextComponent.of(replaceVelocityPlayer(footer, player))
			);
		}
	}

	@Nonnull
	public String replaceVelocityPlayer(@Nonnull String content, @Nonnull Player player) {
		return replacePlayer(content, player.getUniqueId())
			.replace("{service}", !player.getCurrentServer().isPresent() ? "N/A" : player.getCurrentServer().get().getServerInfo().getName())
			.replace("{name}", player.getUsername())
			.replace("{ping}", String.valueOf(player.getPing()))
		;
	}

	@Override
	public void schedule(@Nonnull Runnable command, long millis) {
		plugin.getServer().getScheduler().buildTask(plugin, command).repeat(millis, TimeUnit.MILLISECONDS);
	}

	public ServerPing getMotd(@Nonnull ServerPing original) {
		CloudDriver cloud = CloudDriver.getInstance();
		ProxyMotdEntryConfig motd = getMotdEntry();
		if (motd == null) return original;

		String motdText = replaceDefault(motd.getFirstLine() + '\n' + motd.getSecondLine());
		String protocolText = motd.getProtocolText() == null ? null : replaceDefault(motd.getProtocolText());

		SamplePlayer[] playerInfo = new SamplePlayer[motd.getPlayerInfo() == null || motd.getPlayerInfo().isEmpty() ? 0 : motd.getPlayerInfo().size()];
		for (int i = 0; i < playerInfo.length; i++) {
			playerInfo[i] = new SamplePlayer(motd.getPlayerInfo().get(i), UUID.randomUUID());
		}

		return new ServerPing(
			new Version(protocolText == null ? original.getVersion().getProtocol() : 1, protocolText == null ? original.getVersion().getName() : protocolText),
			new Players(cloud.getGlobalConfig().getMaxPlayers(), cloud.getPlayerManager().getOnlinePlayerCount(), Arrays.asList(playerInfo)),
			TextComponent.of(motdText),
			original.getFavicon().orElse(null)
		);
	}
}
