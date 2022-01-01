package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.translate.Translatable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudPlayer#getExecutor()
 *
 * @see PlayerManager#getPlayerExecutor(UUID)
 * @see PlayerManager#getGlobalExecutor()
 */
public interface PlayerExecutor {

	/**
	 * @return the uuid of the player this executor is managing
	 *
	 * @see PlayerManager#getPlayerExecutor(UUID)
	 */
	@Nonnull
	UUID getPlayerUniqueId();

	/**
	 * @return whether this executor covers all players
	 *
	 * @see PlayerManager#getGlobalExecutor()
	 */
	boolean isGlobal();

	/**
	 * Sends the message to the player.
	 *
	 * @param message the message to send
	 */
	default void sendMessage(@Nonnull String message) {
		sendMessage(null, message);
	}

	/**
	 * Sends the message to the player.
	 *
	 * @param message the message to send
	 */
	default void sendMessage(@Nullable String permission, @Nonnull String message) {
		sendMessage(permission, new ChatText(message));
	}

	/**
	 * Sends the message to the player.
	 *
	 * @param message the message components to send
	 */
	default void sendMessage(@Nonnull ChatText... message) {
		sendMessage(null, message);
	}

	/**
	 * Sends the messages to the player if the player has the given permission.
	 * The permission check will be done on the proxy the player is on.
	 *
	 * @param permission the permission the player must have
	 * @param message the message components to send
	 */
	void sendMessage(@Nullable String permission, @Nonnull ChatText... message);

	/**
	 * Sends the translation to the player in their selected language and replaces the given arguments.
	 * The translation will be done on the player's proxy, as the player search is done there (due to implementation of global executor)
	 *
	 * @param translation the name of the translation
	 * @param args the arguments to replace
	 *
	 * @see net.anweisen.cloud.driver.translate.TranslatedValue
	 */
	void sendTranslation(@Nonnull String translation, @Nonnull Object... args);

	/**
	 * Sends the translation to the player in their selected language and replaces the given arguments.
	 * The translation will be done on the player's proxy, as the player search is done there (due to implementation of global executor)
	 *
	 * @param translation the name of the translation
	 * @param args the arguments to replace
	 *
	 * @see net.anweisen.cloud.driver.translate.TranslatedValue
	 */
	default void sendTranslation(@Nonnull Translatable translation, @Nonnull Object... args) {
		sendTranslation(translation.getName(), args);
	}

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
	 * Sends the player to the given server.
	 *
	 * @param service the target server
	 */
	default void connect(@Nonnull ServiceInfo service) {
		connect(service.getName());
	}

	/**
	 * Sends the player to a fallback server (Lobby) just like in the /hub command.
	 */
	void connectFallback();

	/**
	 * Disconnects the player from the proxy with the given reason.
	 *
	 * @param kickReason the reason with which the player should be kicked
	 */
	void disconnect(@Nullable String kickReason);

	void chat(@Nonnull String message);

	void performCommand(@Nonnull String command);

}
