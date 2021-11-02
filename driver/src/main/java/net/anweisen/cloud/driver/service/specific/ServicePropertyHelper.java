package net.anweisen.cloud.driver.service.specific;

import javax.annotation.Nonnull;

/**
 * This class contains some common helper methods for
 * processing {@link ServiceProperty ServiceProperties}.
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see ServiceProperty
 */
public final class ServicePropertyHelper {

	private ServicePropertyHelper() {}

	public static boolean isIngame(@Nonnull ServiceInfo service) {
		return "ingame".equalsIgnoreCase(service.get(ServiceProperty.PHASE));
	}

	public static boolean isLobby(@Nonnull ServiceInfo service) {
		return "lobby".equalsIgnoreCase(service.get(ServiceProperty.PHASE));
	}

	public boolean isFull(@Nonnull ServiceInfo service) {
		return service.get(ServiceProperty.MAX_PLAYERS) > 0
			&& service.get(ServiceProperty.ONLINE_PLAYERS) >= service.get(ServiceProperty.MAX_PLAYERS);
	}

	public static boolean isEmpty(@Nonnull ServiceInfo service) {
		return service.get(ServiceProperty.ONLINE_PLAYERS) == 0;
	}

	public boolean isStarting(@Nonnull ServiceInfo service) {
		return service.getState() == ServiceState.RUNNING && !service.isReady() || service.getControlState() == ServiceControlState.STARTING;
	}

}
