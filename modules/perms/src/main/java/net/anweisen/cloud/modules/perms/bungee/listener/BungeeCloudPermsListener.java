package net.anweisen.cloud.modules.perms.bungee.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.utilities.common.misc.ReflectionUtils;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudPermsListener implements Listener {

	@EventHandler
	public void onPermsCheck(@Nonnull PermissionCheckEvent event) {

		try {
			Method method = ReflectionUtils.getInheritedPrivateMethod(event.getSender().getClass(), "getUniqueId");
			method.setAccessible(true);
			UUID uniqueId = (UUID) method.invoke(event.getSender());

			PermissionPlayer player = CloudDriver.getInstance().getPermissionManager().getPlayerByUniqueId(uniqueId);
			if (player == null) return;

			event.setHasPermission(player.hasPermissionHere(event.getPermission()));

		} catch (NoSuchMethodException ex) {
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}

	}

}
