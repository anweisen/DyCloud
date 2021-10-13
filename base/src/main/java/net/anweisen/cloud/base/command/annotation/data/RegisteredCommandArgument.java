package net.anweisen.cloud.base.command.annotation.data;

import net.anweisen.cloud.base.command.completer.CommandCompleter;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class RegisteredCommandArgument {

	private final String name;
	private final Class<? extends CommandCompleter> completerClass;

	public RegisteredCommandArgument(@Nonnull String name, @Nonnull Class<? extends CommandCompleter> completerClass) {
		this.name = name;
		this.completerClass = completerClass;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public Class<? extends CommandCompleter> getCompleterClass() {
		return completerClass;
	}
}
