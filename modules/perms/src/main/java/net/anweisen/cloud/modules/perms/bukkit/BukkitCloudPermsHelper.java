package net.anweisen.cloud.modules.perms.bukkit;

import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.utilities.common.misc.ReflectionUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitCloudPermsHelper {

	public static void injectPermissible(@Nonnull Player player) {
		try {
			Class<?> clazz = player.getClass();
			Field field = ReflectionUtils.getInheritedPrivateField(clazz, "perm");
			field.setAccessible(true);
			field.set(player, new BukkitCloudPermsPermissible(player));
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			ex.printStackTrace();
		}

		if (player.hasPermission(Permissions.AUTO_OP))
			player.setOp(true);
	}

}
