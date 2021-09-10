package net.anweisen.cloud.modules.notify.config;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NotifyConfig {

	private NotifyIngameConfig ingame;
	private NotifyDiscordConfig discord;

	private NotifyConfig() {
	}

	public NotifyConfig(@Nonnull NotifyIngameConfig ingame, @Nonnull NotifyDiscordConfig discord) {
		this.ingame = ingame;
		this.discord = discord;
	}

	@Nonnull
	public NotifyIngameConfig getIngame() {
		return ingame;
	}

	@Nonnull
	public NotifyDiscordConfig getDiscord() {
		return discord;
	}
}
