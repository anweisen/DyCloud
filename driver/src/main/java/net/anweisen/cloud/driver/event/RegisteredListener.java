package net.anweisen.cloud.driver.event;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface RegisteredListener {

	void execute(@Nonnull Event event) throws Exception;

	@Nonnull
	Class<? extends Event> getEventClass();

	@Nonnull
	EventPriority getPriority();

	boolean getIgnoreCancelled();

	@Nonnull
	Object getHolder();

}
