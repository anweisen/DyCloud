package net.anweisen.cloud.driver.event.defaults;

import net.anweisen.cloud.driver.event.Event;
import net.anweisen.cloud.driver.event.EventOrder;
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
	private final EventOrder order;
	private final boolean ignoreCancelled;

	public DefaultRegisteredListener(@Nonnull Object holder, @Nonnull Method method, @Nonnull Class<? extends Event> eventClass, @Nonnull EventOrder order, boolean ignoreCancelled) {
		this.holder = holder;
		this.method = method;
		this.eventClass = eventClass;
		this.order = order;
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
	public EventOrder getOrder() {
		return order;
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