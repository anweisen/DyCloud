package net.anweisen.cloud.driver.network.http.handler;

import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultRegisteredHandler implements RegisteredHandler {

	private final Object holder;
	private final Method method;

	private final String path;
	private final String permission;
	private final HttpMethod[] methods;

	public DefaultRegisteredHandler(@Nonnull Object holder, @Nonnull Method method, @Nonnull String path, @Nonnull String permission, @Nonnull HttpMethod[] methods) {
		this.holder = holder;
		this.method = method;
		this.path = path;
		this.permission = permission;
		this.methods = methods;
	}

	@Override
	public void execute(@Nonnull HttpContext context) throws Exception {
		method.invoke(holder, context);
	}

	@Nonnull
	@Override
	public String getPath() {
		return path;
	}

	@Nonnull
	@Override
	public String getPermission() {
		return permission;
	}

	@Nonnull
	@Override
	public HttpMethod[] getMethods() {
		return methods;
	}

	@Nonnull
	@Override
	public Object getHolder() {
		return holder;
	}
}
