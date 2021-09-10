package net.anweisen.cloud.modules.notify.config;

import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NotifyIngameConfig {

	private boolean enabled;
	private String creatingMessage;
	private String startingMessage;
	private String startedMessage;
	private String stoppedMessage;
	private String deletedMessage;
	private String hoverConnectMessage;

	private NotifyIngameConfig() {
	}

	public NotifyIngameConfig(boolean enabled, @Nullable String creatingMessage, @Nullable String startingMessage, @Nullable String startedMessage, @Nullable String stoppedMessage, @Nullable String deletedMessage, @Nullable String hoverConnectMessage) {
		this.enabled = enabled;
		this.creatingMessage = creatingMessage;
		this.startingMessage = startingMessage;
		this.startedMessage = startedMessage;
		this.stoppedMessage = stoppedMessage;
		this.deletedMessage = deletedMessage;
		this.hoverConnectMessage = hoverConnectMessage;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Nullable
	public String getCreatingMessage() {
		return creatingMessage;
	}

	@Nullable
	public String getStartingMessage() {
		return startingMessage;
	}

	@Nullable
	public String getStartedMessage() {
		return startedMessage;
	}

	@Nullable
	public String getStoppedMessage() {
		return stoppedMessage;
	}

	@Nullable
	public String getDeletedMessage() {
		return deletedMessage;
	}

	@Nullable
	public String getHoverConnectMessage() {
		return hoverConnectMessage;
	}
}
