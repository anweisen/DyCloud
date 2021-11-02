package net.anweisen.cloud.driver.service.specific;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ServicePropertyHelper {

	private ServicePropertyHelper() {}

	public static boolean isIngame(@Nonnull ServiceInfo service) {
		return "ingame".equalsIgnoreCase(service.get(ServiceProperty.PHASE));
	}

	public static boolean isLobby(@Nonnull ServiceInfo service) {
		return "lobby".equalsIgnoreCase(service.get(ServiceProperty.PHASE));
	}

}
