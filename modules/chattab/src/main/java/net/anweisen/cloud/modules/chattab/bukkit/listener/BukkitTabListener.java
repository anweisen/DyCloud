package net.anweisen.cloud.modules.chattab.bukkit.listener;

import net.anweisen.cloud.modules.chattab.bukkit.BukkitCloudChatTabPlugin;
import net.anweisen.cloud.modules.chattab.bukkit.handler.TabHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitTabListener implements Listener {

	@EventHandler
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		TabHandler handler = BukkitCloudChatTabPlugin.getInstance().getManager().getTabHandler();
		if (handler == null) return;
		handler.update();
	}

}
