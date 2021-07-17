package net.anweisen.cloud.driver.event;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

	@Nonnull
	EventPriority priority() default EventPriority.NORMAL;

	boolean ignoreCancelled() default false;

}
