package net.anweisen.cloud.modules.chattab.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChatTabConfig {

	private TabConfig tablist;
	private ChatConfig chat;

	private ChatTabConfig() {
	}

	public ChatTabConfig(@Nonnull TabConfig tablist, @Nonnull ChatConfig chat) {
		this.tablist = tablist;
		this.chat = chat;
	}

	@Nonnull
	public ChatConfig getChat() {
		return chat;
	}

	@Nonnull
	public TabConfig getTablist() {
		return tablist;
	}

	@Override
	public String toString() {
		return "ChatTab[tablist=" + tablist + " chat=" + chat + "]";
	}
}
