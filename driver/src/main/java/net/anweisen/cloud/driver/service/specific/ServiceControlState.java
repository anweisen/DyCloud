package net.anweisen.cloud.driver.service.specific;

/**
 * The state of a service which describes what is currently going on with it, whether its starting, stopping etc.
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see ServiceInfo#getControlState()
 */
public enum ServiceControlState {

	CREATING,
	STARTING,
	STOPPING,
	KILLING,
	RESTARTING,
	DELETING,
	NONE

}
