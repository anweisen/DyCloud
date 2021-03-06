package net.anweisen.cloud.modules.chattab.bukkit.listener;

import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.cloud.modules.chattab.bukkit.BukkitCloudChatTabPlugin;
import net.anweisen.cloud.modules.chattab.bukkit.handler.ChatHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitChatListener implements Listener {

	@EventHandler
	public void onChat(@Nonnull AsyncPlayerChatEvent event) {
		ChatHandler handler = BukkitCloudChatTabPlugin.getInstance().getManager().getChatHandler();
		if (handler == null) return;

		String message = event.getMessage().replace("%", "%%").trim();
		if (event.getPlayer().hasPermission(Permissions.CHAT_COLORS)) {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}

		if (ChatColor.stripColor(message).trim().isEmpty()) {
			event.setCancelled(true);
			return;
		}

		String format = handler.format(event.getPlayer(), message);
		if (format == null) return;

		event.setFormat(format);

	}

}
