package net.anweisen.cloud.base.command.sender.defaults;

import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.command.sender.ConsoleCommandSender;
import net.anweisen.cloud.base.console.Console;
import net.anweisen.cloud.base.console.ConsoleColor;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultConsoleCommandSender implements ConsoleCommandSender {

	@Override
	public void sendMessage(@Nonnull String message) {
		getLogger().info(ConsoleColor.toUncoloredString('§', message));
	}

	@Override
	public void sendMessage(@Nonnull ChatText... message) {
		sendMessage(ChatText.toString(message));
	}

	@Override
	public void sendTranslation(@Nonnull String translation, @Nonnull Object... args) {
		sendTranslation(Translatable.of(translation), args);
	}

	@Override
	public void sendTranslation(@Nonnull Translatable translation, @Nonnull Object... args) {
		sendMessage(translation.translateDefault().asText(args));
	}

	@Override
	public boolean hasPermission(@Nonnull String permission) {
		return true;
	}

	@Nonnull
	@Override
	public Console getConsole() {
		return CloudBase.getInstance().getConsole();
	}

	@Nonnull
	@Override
	public ILogger getLogger() {
		return CloudBase.getInstance().getLogger();
	}

	@Nonnull
	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public String toString() {
		return "ConsoleCommandSender[]";
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof DefaultConsoleCommandSender;
	}

}
