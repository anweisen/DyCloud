package net.anweisen.cloud.driver.event;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getEventManager()
 */
public interface EventManager {

	@Nonnull
	EventManager removeListener(@Nonnull RegisteredListener listener);

	@Nonnull
	EventManager addListener(@Nonnull RegisteredListener listener);

	@Nonnull
	EventManager addListeners(@Nonnull Collection<? extends RegisteredListener> listeners);

	@Nonnull
	EventManager registerListener(@Nonnull Object listener);

	@Nonnull
	default EventManager registerListeners(@Nonnull Object... listeners) {
		for (Object listener : listeners)
			registerListener(listener);
		return this;
	}

	@Nonnull
	default EventManager registerListeners(@Nonnull Iterable<?> listeners) {
		for (Object listener : listeners)
			registerListener(listener);
		return this;
	}

	@Nonnull
	EventManager unregisterListener(@Nonnull Object listener);

	@Nonnull
	default EventManager unregisterListeners(@Nonnull Object... listeners) {
		for (Object listener : listeners)
			unregisterListener(listener);
		return this;
	}

	@Nonnull
	default EventManager unregisterListeners(@Nonnull Iterable<?> listeners) {
		for (Object listener : listeners)
			unregisterListener(listener);
		return this;
	}

	/**
	 * Unregisters all listeners of the given class.
	 */
	@Nonnull
	EventManager unregisterListener(@Nonnull Class<?> listenerClass);

	@Nonnull
	default EventManager unregisterListeners(@Nonnull Class<?>... listenerClasses) {
		for (Class<?> listenerClass : listenerClasses)
			unregisterListener(listenerClass);
		return this;
	}

	/**
	 * Unregisters all listeners which holder's classloader is the given classloader.
	 */
	@Nonnull
	EventManager unregisterListeners(@Nonnull ClassLoader loader);

	@Nonnull
	EventManager unregisterAll();

	@Nonnull
	<E extends Event> E callEvent(@Nonnull E event);

	@Nonnull
	<E extends Event> Task<E> nextEvent(@Nonnull Class<E> eventClass);

	@Nonnull
	default <E extends Event> E awaitNextEvent(@Nonnull Class<E> eventClass) {
		try {
			return nextEvent(eventClass).get();
		} catch (InterruptedException | ExecutionException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nullable
	default <E extends Event> E awaitNextEvent(@Nonnull Class<E> eventClass, long timeout, @Nonnull TimeUnit unit) {
		try {
			return nextEvent(eventClass).get(timeout, unit);
		} catch (InterruptedException | ExecutionException ex) {
			throw new WrappedException(ex);
		} catch (TimeoutException ex) {
			return null;
		}
	}

}
