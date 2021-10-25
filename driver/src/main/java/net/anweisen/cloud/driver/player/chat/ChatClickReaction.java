package net.anweisen.cloud.driver.player.chat;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ChatClickReaction {

	RUN_COMMAND("run"),
	SUGGEST_COMMAND("suggest"),
	OPEN_URL("open");

	private final String shortcut;

	ChatClickReaction(@Nonnull String shortcut) {
		this.shortcut = shortcut;
	}

	@Nonnull
	public String getShortCut() {
		return shortcut;
	}

	@Nonnull
	public static ChatClickReaction getByShortcut(@Nonnull String shortcut) {
		for (ChatClickReaction event : values()) {
			if (event.getShortCut().equals(shortcut))
				return event;
		}
		throw new IllegalArgumentException("Unknown ChatEventReaction for '" + shortcut + "'");
	}
}
