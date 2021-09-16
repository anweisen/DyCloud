package net.anweisen.cloud.driver.event.defaults;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.*;
import net.anweisen.utilities.common.collection.ClassWalker;
import net.anweisen.utilities.common.concurrent.task.CompletableTask;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.misc.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultEventManager implements EventManager {

	private final Map<Class<? extends Event>, List<RegisteredListener>> listeners = new LinkedHashMap<>();

	@Nonnull
	@Override
	public EventManager removeListener(@Nonnull RegisteredListener listener) {
		listeners.forEach((clazz, listeners) -> listeners.removeIf(current -> current == listener));
		return this;
	}

	@Nonnull
	@Override
	public EventManager addListener(@Nonnull RegisteredListener listener) {
		return addListeners(Collections.singletonList(listener));
	}

	@Nonnull
	@Override
	public EventManager addListeners(@Nonnull Collection<? extends RegisteredListener> listeners) {
		for (RegisteredListener listener : listeners) {
			List<RegisteredListener> registeredListeners = this.listeners.computeIfAbsent(listener.getEventClass(), key -> new LinkedList<>());
			registeredListeners.add(listener);
			registeredListeners.sort(Comparator.comparingInt(value -> value.getOrder().ordinal()));
		}
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
			addListener(new DefaultRegisteredListener(listener, method, parameterType.asSubclass(Event.class), annotation.order(), annotation.ignoreCancelled()));
		}

		return this;
	}

	@Nonnull
	@Override
	public EventManager unregisterListener(@Nonnull Object holder) {
		listeners.forEach((eventClass, listeners) -> listeners.removeIf(listener -> listener.getHolder() == listener));
		return this;
	}

	@Nonnull
	@Override
	public EventManager unregisterListener(@Nonnull Class<?> listenerClass) {
		listeners.forEach((eventClass, listeners) -> listeners.removeIf(listener -> listener.getHolder().getClass() == listenerClass));
		return this;
	}

	@Nonnull
	@Override
	public EventManager unregisterListeners(@Nonnull ClassLoader loader) {
		listeners.forEach((eventClass, listeners) -> listeners.removeIf(listener -> listener.getHolder().getClass().getClassLoader().equals(loader)));
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
		CloudDriver.getInstance().getLogger().trace("Calling event {} | {}", event.getClass().getSimpleName(), event);

		for (Class<?> clazz : ClassWalker.walk(event.getClass())) {
			List<RegisteredListener> listeners = this.listeners.get(clazz);
			if (listeners == null) continue;
			for (RegisteredListener listener : listeners) {
				if (listener.getIgnoreCancelled() && event instanceof Cancelable && ((Cancelable)event).isCancelled()) continue;

				try {
					listener.execute(event);
				} catch (Throwable ex) {
					CloudDriver.getInstance().getLogger().error("An error uncaught occurred while executing event listener", ex);
					if (ex instanceof Error)
						throw (Error) ex;
				}
			}
		}

		if (event instanceof Cancelable)
			CloudDriver.getInstance().getLogger().trace("=> {}: cancelled={}", event.getClass().getSimpleName(), ((Cancelable)event).isCancelled());

		return event;
	}

	@Nonnull
	@Override
	public <E extends Event> Task<E> nextEvent(@Nonnull Class<E> eventClass) {
		CompletableTask<E> task = Task.completable();
		RegisteredListener listener = new ActionRegisteredListener<>(eventClass, task::complete);
		addListener(listener);
		task.onComplete(() -> removeListener(listener));
		return task;
	}

}
