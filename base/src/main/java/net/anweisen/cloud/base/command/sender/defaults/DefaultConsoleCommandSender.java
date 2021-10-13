package net.anweisen.cloud.base.command.sender.defaults;

import net.anweisen.cloud.base.command.sender.ConsoleCommandSender;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.ConsoleColor;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.translate.Translatable;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultConsoleCommandSender implements ConsoleCommandSender {

	private final Console console;

	public DefaultConsoleCommandSender(@Nonnull Console console) {
		this.console = console;
	}

	@Override
	public void sendMessage(@Nonnull String message) {
		console.writeLine(ConsoleColor.toColoredString('§', message));
	}

	@Override
	public void sendMessage(@Nonnull ChatText... message) {
		console.writeLine(ConsoleColor.toColoredString('§', ChatText.toString(message)));
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
		return console;
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
}
