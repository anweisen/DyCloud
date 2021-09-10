package net.anweisen.cloud.modules.notify.config;

import net.anweisen.utilities.common.discord.DiscordWebhook;

import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NotifyDiscordConfig {

	private boolean enabled;
	private DiscordWebhook creatingMessage;
	private DiscordWebhook startingMessage;
	private DiscordWebhook startedMessage;
	private DiscordWebhook stoppedMessage;
	private DiscordWebhook deletedMessage;

	private NotifyDiscordConfig() {
	}

	public NotifyDiscordConfig(boolean enabled, @Nullable DiscordWebhook creatingMessage, @Nullable DiscordWebhook startingMessage,
	                           @Nullable DiscordWebhook startedMessage, @Nullable DiscordWebhook stoppedMessage, @Nullable DiscordWebhook deletedMessage) {
		this.enabled = enabled;
		this.creatingMessage = creatingMessage;
		this.startingMessage = startingMessage;
		this.startedMessage = startedMessage;
		this.stoppedMessage = stoppedMessage;
		this.deletedMessage = deletedMessage;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Nullable
	public DiscordWebhook getCreatingMessage() {
		return creatingMessage;
	}

	@Nullable
	public DiscordWebhook getStartingMessage() {
		return startingMessage;
	}

	@Nullable
	public DiscordWebhook getStartedMessage() {
		return startedMessage;
	}

	@Nullable
	public DiscordWebhook getStoppedMessage() {
		return stoppedMessage;
	}

	@Nullable
	public DiscordWebhook getDeletedMessage() {
		return deletedMessage;
	}
}
