package net.anweisen.cloud.driver.event;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum EventPriority {

	/** Executed last, after {@link #HIGH} */
	HIGHEST,
	/** Executed after {@link #NORMAL} */
	HIGH,
	/** Executed after {@link #LOW}, default value */
	NORMAL,
	/** Executed after {@link #LOWEST} */
	LOW,
	/** Executed first */
	LOWEST,

}
