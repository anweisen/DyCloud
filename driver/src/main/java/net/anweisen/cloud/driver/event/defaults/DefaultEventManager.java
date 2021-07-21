package net.anweisen.cloud.driver.event.defaults;

import net.anweisen.cloud.driver.event.*;
import net.anweisen.utilities.common.misc.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultEventManager implements EventManager {

	private final List<RegisteredListener> listeners = new CopyOnWriteArrayList<>();

	@Nonnull
	@Override
	public EventManager addListener(@Nonnull RegisteredListener listener) {
		return addListeners(Collections.singletonList(listener));
	}

	@Nonnull
	@Override
	public EventManager addListeners(@Nonnull Collection<? extends RegisteredListener> listeners) {
		this.listeners.addAll(listeners);
		this.listeners.sort(Comparator.comparingInt(value -> value.getPriority().ordinal()));
		return this;
	}

	@Nonnull
	@Override
	public EventManager registerListener(@Nonnull Object listener) {
		for (Method method : ReflectionUtils.getMethodsAnnotatedWith(listener.getClass(), EventListener.class)) {

			if (method.getParameterCount() != 1 || !Modifier.isPublic(method.getModifiers())) {
				throw new IllegalArgumentException(String.format(
					"Listener method %s:%s has to be public with exactly one argument",
					listener.getClass().getName(),
					method.getName()
				));
			}

			Class<?> parameterType = method.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(parameterType)) {
				throw new IllegalArgumentException(String.format(
					"Parameter type %s of listener method %s:%s is not an event",
					parameterType.getName(),
					listener.getClass().getName(),
					method.getName()
				));
			}

			EventListener annotation = method.getAnnotation(EventListener.class);
			addListener(new DefaultRegisteredListener(listener, method, parameterType.asSubclass(Event.class), annotation.priority(), annotation.ignoreCancelled()));
		}

		return this;
	}

	@Nonnull
	@Override
	public EventManager unregisterListener(@Nonnull Object holder) {
		listeners.removeIf(listener -> listener.getHolder() == listener);
		return this;
	}

	@Nonnull
	@Override
	public EventManager unregisterListener(@Nonnull Class<?> listenerClass) {
		listeners.removeIf(listener -> listener.getHolder().getClass() == listenerClass);
		return this;
	}

	@Nonnull
	@Override
	public EventManager unregisterListeners(@Nonnull ClassLoader loader) {
		listeners.removeIf(listener -> listener.getHolder().getClass().getClassLoader().equals(loader));
		return this;
	}

	@Nonnull
	@Override
	public EventManager unregisterAll() {
		listeners.clear();
		return this;
	}

	@Nonnull
	@Override
	public <E extends Event> E callEvent(@Nonnull E event) {
		for (RegisteredListener listener : listeners) {
			if (!listener.getEventClass().isAssignableFrom(event.getClass())) continue;
			if (listener.getIgnoreCancelled() && event instanceof Cancelable && ((Cancelable)event).isCancelled()) continue;

			listener.execute(event);
		}

		return event;
	}

}
