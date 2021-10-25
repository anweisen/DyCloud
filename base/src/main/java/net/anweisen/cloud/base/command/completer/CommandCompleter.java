package net.anweisen.cloud.base.command.completer;

import net.anweisen.cloud.base.command.annotation.CommandArgument;
import net.anweisen.cloud.base.command.sender.CommandSender;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CommandArgument#completer()
 */
public interface CommandCompleter {

	@Nonnull
	Collection<String> complete(@Nonnull CommandSender sender, @Nonnull String message, @Nonnull String argument);

}
