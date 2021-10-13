package net.anweisen.cloud.base.command.sender;

import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.translate.Translatable;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see PlayerCommandSender
 * @see ConsoleCommandSender
 */
public interface CommandSender {

	void sendMessage(@Nonnull String message);

	void sendMessage(@Nonnull ChatText... message);

	void sendTranslation(@Nonnull String translation, @Nonnull Object... args);

	void sendTranslation(@Nonnull Translatable translation, @Nonnull Object... args);

	boolean hasPermission(@Nonnull String permission);

	@Nonnull
	String getName();

}
