package net.anweisen.cloud.modules.chattab.bukkit.handler;

import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultTabFormatter implements TabFormatter {

	@Nonnull
	@Override
	public Format format(@Nonnull Player observator, @Nonnull Player player, @Nonnull PermissionPlayer permissionPlayer, @Nonnull PermissionGroup permissionGroup) {
		return new Format(
			permissionGroup.getTabPrefix() + player.getName(),
			permissionGroup.getNamePrefix() + player.getName(),
			permissionGroup.getNamePrefix(),
			"",
			null,
			permissionGroup.getSortId(),
			true
		);
	}

}
