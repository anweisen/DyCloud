package net.anweisen.cloud.modules.chattab.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class TabConfig {

	private boolean enabled;
	private String prefix;
	private String suffix;

	private TabConfig() {
	}

	public TabConfig(boolean enabled, @Nonnull String prefix, @Nonnull String suffix) {
		this.enabled = enabled;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Nonnull
	public String getPrefix() {
		return prefix;
	}

	@Nonnull
	public String getSuffix() {
		return suffix;
	}

	@Override
	public String toString() {
		return "Tab[enabled=" + enabled + "]";
	}
}
