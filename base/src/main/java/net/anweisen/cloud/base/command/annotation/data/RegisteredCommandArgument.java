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
	private final int words;
	private final boolean raw;

	public RegisteredCommandArgument(@Nonnull String name, @Nonnull Class<? extends CommandCompleter> completerClass, int words, boolean raw) {
		this.name = name;
		this.completerClass = completerClass;
		this.words = words;
		this.raw = raw;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public Class<? extends CommandCompleter> getCompleterClass() {
		return completerClass;
	}

	public int getWords() {
		return words;
	}

	public boolean getRaw() {
		return raw;
	}
}
