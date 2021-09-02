package net.anweisen.cloud.modules.chattab.bukkit;

import net.anweisen.cloud.modules.chattab.bukkit.handler.ChatHandler;
import net.anweisen.cloud.modules.chattab.bukkit.handler.TabHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitCloudChatTabManager {

	private ChatHandler chatHandler;
	private TabHandler tabHandler;

	public void setChatHandler(@Nonnull ChatHandler chatHandler) {
		this.chatHandler = chatHandler;
	}

	public void setTabHandler(@Nonnull TabHandler tabHandler) {
		this.tabHandler = tabHandler;
	}

	@Nullable
	public ChatHandler getChatHandler() {
		return chatHandler;
	}

	@Nullable
	public TabHandler getTabHandler() {
		return tabHandler;
	}
}
