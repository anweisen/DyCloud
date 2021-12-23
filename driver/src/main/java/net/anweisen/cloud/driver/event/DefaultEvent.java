package net.anweisen.cloud.driver.event;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultEvent implements Event {

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[]";
	}

}
