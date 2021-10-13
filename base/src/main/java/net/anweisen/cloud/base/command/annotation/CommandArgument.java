package net.anweisen.cloud.base.command.annotation;

import net.anweisen.cloud.base.command.completer.CommandCompleter;
import net.anweisen.cloud.base.command.completer.EmptyCommandCompleter;

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

	@Nonnull
	String value();

	@Nonnull
	Class<? extends CommandCompleter> completer() default EmptyCommandCompleter.class;

}
