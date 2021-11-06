package net.anweisen.cloud.driver.network.http.handler;

import net.anweisen.cloud.driver.network.http.HttpMethod;

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
public @interface HttpEndpoint {

	@Nonnull
	HttpMethod[] method();

	@Nonnull
	String path() default "";

	@Nonnull
	String permission() default "";

}
