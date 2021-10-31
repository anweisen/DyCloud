package net.anweisen.cloud.modules.bridge.helper;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.global.objects.CommandObject;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceProperty;
import net.anweisen.cloud.wrapper.CloudWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BridgeProxyHelper {

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

	private static BridgeProxyMethods methods;

	@Nonnull
	public static BridgeProxyMethods getMethods() {
		return methods;
	}

	public static void setMethods(@Nonnull BridgeProxyMethods methods) {
		Preconditions.checkNotNull(methods);
		BridgeProxyHelper.methods = methods;
	}

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

	public static void updateCommands() {
		Collection<CommandObject> commands = CloudDriver.getInstance().getGlobalConfig().getIngameCommands();

		Map<String, Collection<CommandObject>> mapping = new LinkedHashMap<>();
		for (CommandObject command : commands)
			mapping.computeIfAbsent(command.getPath().split(" ")[0], key -> new ArrayList<>()).add(command);

		methods.updateCommands(mapping);
	}

	public static void checkPlayerDisconnects() {
		for (CloudPlayer player : CloudDriver.getInstance().getPlayerManager().getOnlinePlayers()) {
			if (player.getProxy().getUniqueId().equals(CloudWrapper.getInstance().getServiceInfo().getUniqueId())) {
				methods.checkPlayerDisconnect(player);
			}
		}
	}

	public static void registerServer(@Nonnull ServiceInfo service) {
		methods.registerServer(service);
	}

	public static void unregisterServer(@Nonnull String name) {
		methods.unregisterServer(name);
	}

	private BridgeProxyHelper() {}

}
