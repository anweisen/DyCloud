package net.anweisen.cloud.base.command;

import net.anweisen.cloud.base.command.annotation.data.RegisteredCommand;
import net.anweisen.cloud.base.command.completer.CommandCompleter;
import net.anweisen.cloud.base.command.sender.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CommandManager {

	void executeCommand(@Nonnull CommandSender sender, @Nonnull String input);

	void registerCommand(@Nonnull Object command);

	default void registerCommands(@Nonnull Object... commands) {
		for (Object command : commands)
			registerCommand(command);
	}

	void unregisterCommand(@Nonnull Object command);

	default void unregisterCommands(@Nonnull Object... commands) {
		for (Object command : commands)
			unregisterCommand(command);
	}

	void unregisterCommand(@Nonnull ClassLoader classLoader);

	void unregisterCommand(@Nonnull String name);

	default void unregisterCommands(@Nonnull String... names) {
		for (String name : names)
			unregisterCommand(name);
	}

	@Nonnull
	Collection<RegisteredCommand> getCommands();

	@Nonnull
	Collection<RegisteredCommand> matchCommandName(@Nonnull String name);

	@Nullable
	RegisteredCommand selectCommand(@Nonnull String input);

	@Nonnull
	Collection<String> completeCommand(@Nonnull CommandSender sender, @Nonnull String input);

	@Nonnull
	CommandCompleter getCompleter(@Nonnull Class<? extends CommandCompleter> completerClass);

}
