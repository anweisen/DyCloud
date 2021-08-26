package net.anweisen.cloud.modules.perms.bukkit.listener;

import net.anweisen.cloud.modules.perms.bukkit.BukkitCloudPermsHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitCloudPermsListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(@Nonnull PlayerLoginEvent event) {
		BukkitCloudPermsHelper.injectPermissible(event.getPlayer());
	}

}
