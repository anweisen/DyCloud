package net.anweisen.cloud.base.command.sender;

import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.command.sender.defaults.DefaultConsoleCommandSender;
import net.anweisen.cloud.base.console.Console;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface ConsoleCommandSender extends CommandSender {

	ConsoleCommandSender INSTANCE = new DefaultConsoleCommandSender(CloudBase.getInstance().getConsole());

	@Nonnull
	Console getConsole();

}
