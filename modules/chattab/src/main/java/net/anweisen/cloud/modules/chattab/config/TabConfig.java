package net.anweisen.cloud.modules.chattab.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class TabConfig {

	private boolean enabled;
	private String prefix;

	private TabConfig() {
	}

	public TabConfig(boolean enabled, @Nonnull String prefix) {
		this.enabled = enabled;
		this.prefix = prefix;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Nonnull
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String toString() {
		return "Tab[enabled=" + enabled + "]";
	}
}
