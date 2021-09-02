package net.anweisen.cloud.modules.chattab.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChatConfig {

	private boolean enabled;
	private String format;

	private ChatConfig() {
	}

	public ChatConfig(boolean enabled, @Nonnull String format) {
		this.enabled = enabled;
		this.format = format;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Nonnull
	public String getFormat() {
		return format;
	}

	@Override
	public String toString() {
		return "Chat[enabled=" + enabled + " format=" + format + "]";
	}
}
