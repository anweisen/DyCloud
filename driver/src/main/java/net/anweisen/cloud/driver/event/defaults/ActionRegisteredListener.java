package net.anweisen.cloud.driver.event.defaults;

import net.anweisen.cloud.driver.event.Event;
import net.anweisen.cloud.driver.event.EventPriority;
import net.anweisen.cloud.driver.event.RegisteredListener;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ActionRegisteredListener<E extends Event> implements RegisteredListener {

	private final Class<E> eventClass;
	private final Consumer<? super E> action;

	public ActionRegisteredListener(@Nonnull Class<E> eventClass, @Nonnull Consumer<? super E> action) {
		this.eventClass = eventClass;
		this.action = action;
	}

	@Override
	public void execute(@Nonnull Event event) {
		action.accept(eventClass.cast(event));
	}

	@Nonnull
	@Override
	public Class<? extends Event> getEventClass() {
		return eventClass;
	}

	@Nonnull
	@Override
	public EventPriority getPriority() {
		return EventPriority.NORMAL;
	}

	@Override
	public boolean getIgnoreCancelled() {
		return false;
	}

	@Nonnull
	@Override
	public Object getHolder() {
		return this;
	}

}
