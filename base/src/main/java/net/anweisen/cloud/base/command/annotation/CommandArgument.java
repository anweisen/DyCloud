package net.anweisen.cloud.base.command.annotation;

import net.anweisen.cloud.base.command.completer.CommandCompleter;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArgument {

	/**
	 * @return the argument's name used in the {@link CommandPath}
	 */
	@Nonnull
	String value();

	/**
	 * The class of the argument completer.
	 * Can only be used with {@link #words() words} {@code = 1}
	 *
	 * @return the class of the argument completer
	 */
	@Nonnull
	Class<? extends CommandCompleter> completer() default CommandCompleter.EMPTY;

	/**
	 * The amount of words used for this argument or {@code -1} for all words left
	 *
	 * @return the amount of words used or {@code -1} for all remaining
	 */
	int words() default 1;

	/**
	 * Whether to use the raw suggestions supplied by the completer.
	 * If not, they will be sorted alphabetically and filtered out when they don't start with the current input.
	 *
	 * @return whether to use the raw suggestions supplied by the completer
	 */
	boolean raw() default false;

}
