package net.anweisen.cloud.driver.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerExecutor {

	/**
	 * @return the uuid of the player this executor is managing
	 */
	@Nonnull
	UUID getPlayerUniqueId();

	/**
	 * Sends a message to the player.
	 *
	 * @param message the message
	 */
	default void sendMessage(@Nonnull String message) {
		sendMessages(message);
	}

	/**
	 * Sends messages to the player.
	 *
	 * @param messages the message lines to send
	 */
	default void sendMessages(@Nonnull String... messages) {
		sendConditionalMessage(null, messages);
	}

	/**
	 * Sends messages to the player if the player has the given permission.
	 * The permission check will be done on the proxy the player is on.
	 *
	 * @param permission the permission the player must have
	 * @param messages the message lines to send
	 */
	void sendConditionalMessage(@Nullable String permission, @Nonnull String... messages);

	/**
	 * Sends an action to the player.
	 *
	 * @param message the message to send
	 */
	void sendActionbar(@Nonnull String message);

	/**
	 * Sends a title to the player.
	 *
	 * @param title the first title line
	 * @param subtitle the second title line
	 * @param fadeIn the time the title will fade in given in ticks
	 * @param stay the time the title will stay on the screen after fading in before fading out given in ticks
	 * @param fadeOut the time the title will fade out after the stay time expired
	 */
	void sendTitle(@Nonnull String title, @Nonnull String subtitle, int fadeIn, int stay, int fadeOut);

	/**
	 * Sends the player to the given server.
	 *
	 * @param serverName the target server's name
	 */
	void connect(@Nonnull String serverName);

	/**
	 * Disconnects the player from the proxy.
	 *
	 * @param kickReason the reason with which the player should be kicked
	 */
	void disconnect(@Nullable String kickReason);

}
