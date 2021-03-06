package net.anweisen.cloud.modules.perms.bukkit;

import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.utility.common.misc.ReflectionUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BukkitCloudPermsHelper {

	public static void injectPermissible(@Nonnull Player player) {
		try {
			Field field = ReflectionUtils.getInheritedPrivateField(player.getClass(), "perm");
			field.setAccessible(true);
			field.set(player, new BukkitCloudPermsPermissible(player));
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			ex.printStackTrace();
		}

		if (player.hasPermission(Permissions.AUTO_OP))
			player.setOp(true);
	}

	private BukkitCloudPermsHelper() {
	}
}
