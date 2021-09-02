package net.anweisen.cloud.modules.proxy.helper;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.cloud.modules.proxy.config.ProxyConfig;
import net.anweisen.cloud.modules.proxy.config.ProxyTabListEntryConfig;
import net.anweisen.utilities.common.misc.StringUtils;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class AbstractCloudProxyManager {

	protected int tablistAnimationIndex = 0;

	protected ProxyConfig config;

	protected String header;
	protected String footer;

	public abstract void updateTabList();

	public abstract void schedule(@Nonnull Runnable command, long millis);

	public void init() {
		config = CloudDriver.getInstance().getGlobalConfig().get("proxyConfig", ProxyConfig.class);
		scheduleTabList();
	}

	protected void scheduleTabList() {
		schedule(this::updateTabList0, (long) (config.getTablist().getAnimationInterval() * 1000));
	}

	protected final void updateTabList0() {
		if (config.getTablist().getFrames().size() >= tablistAnimationIndex++)
			tablistAnimationIndex = 0;

		ProxyTabListEntryConfig frame = config.getTablist().getFrames().get(tablistAnimationIndex);

		header = StringUtils.getIterableAsString(frame.getHeader(), "\n", Function.identity());
		footer = StringUtils.getIterableAsString(frame.getFooter(), "\n", Function.identity());

		updateTabList();
	}

	protected String replaceDefault(@Nonnull String content, @Nonnull UUID playerUniqueId) {
		if (CloudDriver.getInstance().hasPermissionManager()) {
			PermissionPlayer permissionPlayer = CloudDriver.getInstance().getPermissionManager().getPlayerByUniqueId(playerUniqueId);
			PermissionGroup group = permissionPlayer.getHighestGroupOptional().orElseGet(() -> CloudDriver.getInstance().getPermissionManager().getDefaultGroup());
			if (group != null) {
				content = content
					.replace("{group.name}", group.getName())
					.replace("{group.color}", group.getColor())
					.replace("{group.display}", group.getDisplayName())
				;
			}
		}

		return content
			.replace("{players.online}", CloudDriver.getInstance().getPlayerManager().getOnlinePlayerCount() + "")
			.replace("{players.max}", CloudDriver.getInstance().getGlobalConfig().getMaxPlayers() + "")
		;
	}
}
