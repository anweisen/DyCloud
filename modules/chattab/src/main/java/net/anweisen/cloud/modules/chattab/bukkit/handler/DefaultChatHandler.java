package net.anweisen.cloud.modules.chattab.bukkit.handler;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.cloud.modules.chattab.bukkit.BukkitCloudChatTabPlugin;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultChatHandler implements ChatHandler {

	@Nonnull
	@Override
	public String format(@Nonnull Player player, @Nonnull String message) {
		String format = BukkitCloudChatTabPlugin.getInstance().getChatTabConfig().getChat().getFormat();

		PermissionPlayer permissionPlayer = CloudDriver.getInstance().getPermissionManager().getPlayerByUniqueId(player.getUniqueId());
		PermissionGroup group = permissionPlayer.getHighestGroup();
		if (group != null) {
			format = format
				.replace("{group.name}", group.getName())
				.replace("{group.color}", group.getColor())
				.replace("{group.display}", group.getDisplayName())
			;
		}

		return format
			.replace("{message}", message)
			.replace("{player.name}", player.getName())
			.replace("{player.display}", player.getDisplayName())
			.replace("{player.uuid}", player.getUniqueId().toString())
		;
	}
}
