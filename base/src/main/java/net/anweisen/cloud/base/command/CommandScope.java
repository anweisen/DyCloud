package net.anweisen.cloud.base.command;

import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.base.command.sender.ConsoleCommandSender;
import net.anweisen.cloud.base.command.sender.PlayerCommandSender;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum CommandScope {

	CONSOLE,
	CONSOLE_AND_INGAME,
	INGAME;

	private final boolean console, ingame;

	CommandScope() {
		console = name().contains("CONSOLE");
		ingame = name().contains("INGAME");
	}

	public boolean isConsole() {
		return console;
	}

	public boolean isIngame() {
		return ingame;
	}

	public boolean hasCloudPrefix() {
		return this == CONSOLE_AND_INGAME;
	}

	public boolean covers(@Nonnull CommandSender sender) {
		if (sender instanceof PlayerCommandSender && isIngame()) return true;
		if (sender instanceof ConsoleCommandSender && isConsole()) return true;
		return false;
	}

}
