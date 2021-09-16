package net.anweisen.cloud.driver.event;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum EventOrder {

	/** Executed last, after {@link #LATE} */
	LAST,
	/** Executed after {@link #NORMAL} */
	LATE,
	/** Executed after {@link #EARLY}, default value */
	NORMAL,
	/** Executed after {@link #FIRST} */
	EARLY,
	/** Executed first */
	FIRST,

}
