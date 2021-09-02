package net.anweisen.cloud.modules.notify.config;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NotifyConfig {

	private String startingMessage;
	private String startedMessage;
	private String stoppedMessage;
	private String hoverMessage;

	private NotifyConfig() {
	}

	public NotifyConfig(@Nonnull String startingMessage, @Nonnull String startedMessage, @Nonnull String stoppedMessage, @Nonnull String hoverMessage) {
		this.startingMessage = startingMessage;
		this.startedMessage = startedMessage;
		this.stoppedMessage = stoppedMessage;
		this.hoverMessage = hoverMessage;
	}

	@Nonnull
	public String getStartingMessage() {
		return startingMessage;
	}

	@Nonnull
	public String getStartedMessage() {
		return startedMessage;
	}

	@Nonnull
	public String getStoppedMessage() {
		return stoppedMessage;
	}

	@Nonnull
	public String getHoverMessage() {
		return hoverMessage;
	}

	@Override
	public String toString() {
		return "NotifyConfig[" +
			"startingMessage='" + startingMessage + "'" +
			" startedMessage='" + startedMessage + "'" +
			" stoppedMessage='" + stoppedMessage + "'" +
			" hoverMessage='" + hoverMessage + "'" +
			']';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NotifyConfig that = (NotifyConfig) o;
		return Objects.equals(startingMessage, that.startingMessage) && Objects.equals(startedMessage, that.startedMessage) && Objects.equals(stoppedMessage, that.stoppedMessage) && Objects.equals(hoverMessage, that.hoverMessage);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startingMessage, startedMessage, stoppedMessage, hoverMessage);
	}
}
