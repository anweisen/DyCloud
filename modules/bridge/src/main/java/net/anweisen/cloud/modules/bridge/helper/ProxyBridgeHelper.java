package net.anweisen.cloud.modules.bridge.helper;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ProxyBridgeHelper {

	private static final Map<UUID, PlayerFallbackHistory> playerFallbacks = new ConcurrentHashMap<>();
	private static final Comparator<ServiceInfo> fallbackServiceComparator = (service1, service2) -> {
		ServiceTask task1 = service1.findTask();
		ServiceTask task2 = service2.findTask();
		if (task1.getFallbackPriority() != task2.getFallbackPriority())
			return task2.getFallbackPriority() - task1.getFallbackPriority();

		int online1 = service1.get(ServiceProperty.ONLINE_PLAYERS);
		int online2 = service2.get(ServiceProperty.ONLINE_PLAYERS);
		if (online1 != online2)
			return online1 - online2; // we prefer fewer players

		return 0;
	};

	public static void clearFallbackHistory(@Nonnull UUID playerUUID) {
		playerFallbacks.remove(playerUUID);
	}

	@Nonnull
	public static PlayerFallbackHistory getOrCreateFallbackHistory(@Nonnull UUID playerUUID) {
		return playerFallbacks.computeIfAbsent(playerUUID, key -> new PlayerFallbackHistory());
	}

	@Nullable
	public static ServiceInfo getNextFallback(@Nonnull UUID playerUUID, @Nonnull Predicate<String> permissionTester) {
		PlayerFallbackHistory history = getOrCreateFallbackHistory(playerUUID);
		return getAvailableFallbacks(permissionTester)
			.filter(service -> !history.getFailedConnections().contains(service.getName()))
			.min(fallbackServiceComparator)
			.map(service -> {
				history.setPendingConnectionAttempt(service.getName());
				return service;
			}).orElse(null);
	}

	@Nonnull
	public static Stream<ServiceInfo> getAvailableFallbacks(@Nonnull Predicate<String> permissionTester) {
		return CloudDriver.getInstance().getServiceManager().getServiceInfos().stream()
			.filter(service -> {
				ServiceTask task = service.findTask();
				return service.isReady()
					&& task != null && task.isFallback()
					&& (task.getPermission() == null || permissionTester.test(task.getPermission()));
			})
			.sorted(fallbackServiceComparator);
	}

	private ProxyBridgeHelper() {}

}
