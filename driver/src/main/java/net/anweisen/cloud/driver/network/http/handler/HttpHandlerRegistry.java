package net.anweisen.cloud.driver.network.http.handler;

import net.anweisen.utility.common.misc.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class HttpHandlerRegistry {

	private final Collection<RegisteredHandler> handlers = new CopyOnWriteArrayList<>();

	public void removeHandler(@Nonnull RegisteredHandler handler) {
		handlers.remove(handler);
	}

	public void addHandler(@Nonnull RegisteredHandler handler) {
		handlers.add(handler);
	}

	@Nonnull
	public HttpHandlerRegistry registerHandler(@Nonnull Object handler) {
		return registerHandler("", handler);
	}

	@Nonnull
	public HttpHandlerRegistry registerHandler(@Nonnull String pathPrefix, @Nonnull Object handler) {
		HttpRouter routerAnnotation = handler.getClass().getAnnotation(HttpRouter.class);

		if (pathPrefix.equals("/"))
			pathPrefix = "";
		if (!pathPrefix.endsWith("/"))
			pathPrefix = pathPrefix + "/";

		for (Method method : ReflectionUtils.getMethodsAnnotatedWith(handler.getClass(), HttpEndpoint.class)) {
			HttpEndpoint endpointAnnotation = method.getAnnotation(HttpEndpoint.class);

			String path = pathPrefix + routerAnnotation.value();
			if (!path.startsWith("/"))
				path = "/" + path;
			if (path.endsWith("/") && !path.equals("/"))
				path = path.substring(0, path.length() - 1);

			String additionalPath = endpointAnnotation.path();
			if (!additionalPath.startsWith("/") && !additionalPath.isEmpty())
				additionalPath = "/" + additionalPath;
			if (additionalPath.endsWith("/"))
				additionalPath = additionalPath.substring(0, additionalPath.length() - 1);

			String fullPath = path + additionalPath;

			addHandler(new DefaultRegisteredHandler(handler, method, fullPath, endpointAnnotation.permission(), endpointAnnotation.method()));
		}

		return this;
	}

	@Nonnull
	public HttpHandlerRegistry registerHandlers(@Nonnull Object... handlers) {
		for (Object handler : handlers)
			registerHandler(handler);
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry registerHandlers(@Nonnull String pathPrefix, @Nonnull Object... handlers) {
		for (Object handler : handlers)
			registerHandler(pathPrefix, handler);
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry unregisterListener(@Nonnull Object listener) {
		handlers.removeIf(handler -> handler.getHolder() == listener);
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry unregisterListeners(@Nonnull Object... listeners) {
		for (Object listener : listeners)
			unregisterListener(listener);
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry unregisterListeners(@Nonnull Iterable<?> listeners) {
		for (Object listener : listeners)
			unregisterListener(listener);
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry unregisterListener(@Nonnull Class<?> listenerClass) {
		handlers.removeIf(handler -> handler.getHolder().getClass() == listenerClass);
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry unregisterListeners(@Nonnull Class<?>... listenerClasses) {
		for (Class<?> listenerClass : listenerClasses)
			unregisterListener(listenerClass);
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry unregisterListeners(@Nonnull ClassLoader loader) {
		handlers.removeIf(handler -> handler.getHolder().getClass().getClassLoader().equals(loader));
		return this;
	}

	@Nonnull
	public HttpHandlerRegistry unregisterAll() {
		handlers.clear();
		return this;
	}

	@Nonnull
	public Collection<RegisteredHandler> getHandlers() {
		return handlers;
	}
}
