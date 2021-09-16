package net.anweisen.cloud.modules.perms.velocity;

import com.velocitypowered.api.proxy.Player;
import net.anweisen.utilities.common.misc.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class VelocityCloudPermsHelper {

	public static void injectFunction(@Nonnull Player player) {
		try {
			Class<?> clazz = player.getClass();
			Field field = ReflectionUtils.getInheritedPrivateField(clazz, "permissionFunction");
			field.setAccessible(true);
			field.set(player, new VelocityCloudPermsFunction(player));
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}

	private VelocityCloudPermsHelper() {
	}
}
