package net.anweisen.cloud.driver.event.defaults;

import net.anweisen.cloud.driver.event.Event;
import net.anweisen.cloud.driver.event.EventPriority;
import net.anweisen.cloud.driver.event.RegisteredListener;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class DefaultRegisteredListener implements RegisteredListener {

	private final Object holder;
	private final Method method;
	private final Class<? extends Event> eventClass;
	private final EventPriority priority;
	private final boolean ignoreCancelled;

	public DefaultRegisteredListener(@Nonnull Object holder, @Nonnull Method method, @Nonnull Class<? extends Event> eventClass, @Nonnull EventPriority priority, boolean ignoreCancelled) {
		this.holder = holder;
		this.method = method;
		this.eventClass = eventClass;
		this.priority = priority;
		this.ignoreCancelled = ignoreCancelled;
	}

	public void execute(@Nonnull Event event) throws Exception {
		method.invoke(holder, event);
	}

	@Nonnull
	@Override
	public Class<? extends Event> getEventClass() {
		return eventClass;
	}

	@Nonnull
	@Override
	public EventPriority getPriority() {
		return priority;
	}

	@Override
	public boolean getIgnoreCancelled() {
		return ignoreCancelled;
	}

	@Nonnull
	public Object getHolder() {
		return holder;
	}

}