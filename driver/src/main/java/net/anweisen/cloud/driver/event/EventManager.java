package net.anweisen.cloud.driver.event;

import net.anweisen.cloud.driver.CloudDriver;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getEventManager()
 */
public interface EventManager {

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

}
