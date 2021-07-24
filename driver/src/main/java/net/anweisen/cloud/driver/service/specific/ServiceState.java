package net.anweisen.cloud.driver.service.specific;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ServiceState {

	/**
	 * The request to create this service was sent
	 * The service is being prepared..
	 */
	DEFINED,

	/**
	 * All templates are loaded and the service is ready to start
	 */
	PREPARED,

	/**
	 * The service was started and is now running
	 */
	RUNNING,

	/**
	 * The service was stopped and may be restarted at some point
	 */
	STOPPED,

	/**
	 * The service is deleted completely and cannot be recovered or started anymore
	 */
	DELETED


}
