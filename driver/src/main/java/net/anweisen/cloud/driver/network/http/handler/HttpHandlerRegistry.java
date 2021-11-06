package net.anweisen.cloud.driver.network.http.handler;

import net.anweisen.utilities.common.misc.ReflectionUtils;

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

	public void registerHandler(@Nonnull Object handler) {
		registerHandler("", handler);
	}

	public void registerHandler(@Nonnull String pathPrefix, @Nonnull Object handler) {
		HttpRouter routerAnnotation = handler.getClass().getAnnotation(HttpRouter.class);

		if (pathPrefix.equals("/"))
			pathPrefix = "";

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
	}

	public void registerHandlers(@Nonnull Object... handlers) {
		for (Object handler : handlers)
			registerHandler(handler);
	}

	public void registerHandlers(@Nonnull String pathPrefix, @Nonnull Object... handlers) {
		for (Object handler : handlers)
			registerHandler(pathPrefix, handler);
	}

	public void unregisterListener(@Nonnull Object listener) {
		handlers.removeIf(handler -> handler.getHolder() == listener);
	}

	public void unregisterListeners(@Nonnull Object... listeners) {
		for (Object listener : listeners)
			unregisterListener(listener);
	}

	public void unregisterListeners(@Nonnull Iterable<?> listeners) {
		for (Object listener : listeners)
			unregisterListener(listener);
	}

	public void unregisterListener(@Nonnull Class<?> listenerClass) {
		handlers.removeIf(handler -> handler.getHolder().getClass() == listenerClass);
	}

	public void unregisterListeners(@Nonnull Class<?>... listenerClasses) {
		for (Class<?> listenerClass : listenerClasses)
			unregisterListener(listenerClass);
	}

	public void unregisterListeners(@Nonnull ClassLoader loader) {
		handlers.removeIf(handler -> handler.getHolder().getClass().getClassLoader().equals(loader));
	}

	public void unregisterAll() {
		handlers.clear();
	}

	@Nonnull
	public Collection<RegisteredHandler> getHandlers() {
		return handlers;
	}
}
