package net.anweisen.cloud.modules.perms.helper;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PermissionHelper {

	private PermissionHelper() {
	}

	public static boolean hasPermission(@Nonnull PermissionPlayer player, @Nonnull String permission) {
		return player.hasPermission(permission) || player.hasTaskPermission(CloudDriver.getInstance().getComponentName(), permission);
	}

}
