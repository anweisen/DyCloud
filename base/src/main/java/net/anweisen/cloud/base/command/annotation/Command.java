package net.anweisen.cloud.base.command.annotation;

import net.anweisen.cloud.base.command.CommandScope;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	/**
	 * The name (and alternate names) of the command.
	 * Command names cannot contain spaces.
	 */
	@Nonnull
	String[] name();

	@Nonnull
	CommandScope scope();

	/**
	 * Only when the command can be executed ingame.
	 * Empty string for no permission required.
	 */
	@Nonnull
	String permission() default "";

}
