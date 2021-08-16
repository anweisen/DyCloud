package net.anweisen.cloud.modules.bridge.helper;

import net.anweisen.cloud.modules.bridge.bungee.listener.BungeeCloudListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerApiPacket;
import net.anweisen.cloud.driver.network.packet.def.PlayerApiPacket.PlayerActionType;
import net.anweisen.cloud.driver.player.data.PlayerNetworkProxyConnection;
import net.anweisen.cloud.driver.player.data.PlayerNetworkServerConnection;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceProperties;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.anweisen.cloud.wrapper.event.service.ServiceInfoConfigureEvent;

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
public final class BridgeHelper {

	// TODO move to own file?
	public static class PlayerFallbackHistory {

		private final Collection<String> failedConnections = new ArrayList<>();
		private String pendingConnectionAttempt;

		@Nonnull
		public Collection<String> getFailedConnections() {
			return failedConnections;
		}

		@Nullable
		public String getPendingConnectionAttempt() {
			return pendingConnectionAttempt;
		}

		public void addFailedConnection(@Nonnull String serverName) {
			failedConnections.add(serverName);
		}

		public void setPendingConnectionAttempt(@Nonnull String pendingConnectionAttempt) {
			this.pendingConnectionAttempt = pendingConnectionAttempt;
		}

		@Override
		public String toString() {
			return "FallbackHistory[current=" + pendingConnectionAttempt + " tried=" + failedConnections + "]";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PlayerFallbackHistory that = (PlayerFallbackHistory) o;
			return Objects.equals(failedConnections, that.failedConnections) && Objects.equals(pendingConnectionAttempt, that.pendingConnectionAttempt);
		}

		@Override
		public int hashCode() {
			return Objects.hash(failedConnections, pendingConnectionAttempt);
		}
	}

	private static final Map<String, ServiceInfo> cachedServices = new ConcurrentHashMap<>();
	private static final Map<UUID, PlayerFallbackHistory> playerFallbacks = new ConcurrentHashMap<>();
	private static final Comparator<ServiceInfo> fallbackServiceComparator = (service1, service2) -> {
		ServiceTask task1 = service1.findTask();
		ServiceTask task2 = service2.findTask();
		if (task1.getFallbackPriority() != task2.getFallbackPriority())
			return task1.getFallbackPriority() - task2.getFallbackPriority();

		int online1 = service1.getProperties().getInt(ServiceProperties.ONLINE_COUNT);
		int online2 = service2.getProperties().getInt(ServiceProperties.ONLINE_COUNT);
		if (online1 != online2)
			return online1 - online2; // we prefer fewer players

		return 0;
	};

	/**
	 *  @see BungeeCloudListener#onInfoConfigure(ServiceInfoConfigureEvent)
	 */
	private static int maxPlayers, lastOnlineCount;

	public static void removeCachedService(@Nonnull String name) {
		cachedServices.remove(name);
	}

	public static void cacheService(@Nonnull ServiceInfo info) {
		cachedServices.put(info.getName(), info);
	}

	@Nullable
	public static ServiceInfo getCachedService(@Nonnull String name) {
		return cachedServices.get(name);
	}

	@Nonnull
	public static Collection<ServiceInfo> getCachedServices() {
		return cachedServices.values();
	}

	public static void removeFallbackHistory(@Nonnull UUID playerUUID) {
		playerFallbacks.remove(playerUUID);
	}

	@Nonnull
	public static PlayerFallbackHistory getOrCreateFallbackHistory(@Nonnull UUID playerUUID) {
		return playerFallbacks.computeIfAbsent(playerUUID, key -> new PlayerFallbackHistory());
	}

	@Nullable
	public static ServiceInfo getNextFallback(@Nonnull UUID playerUUID, @Nonnull Predicate<String> permissionTester) {
		PlayerFallbackHistory history = getOrCreateFallbackHistory(playerUUID);
		System.out.println("Getting next fallback.. " + history); // TODO
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
		return getCachedServices().stream()
			.filter(service -> {
				ServiceTask task = service.findTask();
				return service.isReady()
					&& task != null && task.isFallback()
					&& (task.getPermission() == null || permissionTester.test(task.getPermission()));
			})
			.sorted(fallbackServiceComparator);
	}

	public static int getMaxPlayers() {
		return maxPlayers;
	}

	public static void setMaxPlayers(int maxPlayers) {
		BridgeHelper.maxPlayers = maxPlayers;
	}

	public static int getLastOnlineCount() {
		return lastOnlineCount;
	}

	public static void setOnlineCount(int lastOnlineCount) {
		BridgeHelper.lastOnlineCount = lastOnlineCount;
	}

	public static void updateServiceInfo() {
		CloudWrapper.getInstance().updateServiceInfo();
	}

	@Nonnull
	public static ServiceInfo getServiceInfo() {
		return CloudWrapper.getInstance().getServiceInfo();
	}

	@Nullable
	public static String sendProxyLoginRequestPacket(@Nonnull PlayerNetworkProxyConnection playerConnection) {
		return Optional.ofNullable(
				CloudWrapper.getInstance().getSocketComponent().getFirstChannel()
				.sendQuery(new PlayerApiPacket(PlayerActionType.PROXY_LOGIN_REQUEST, buffer -> buffer.writeObject(playerConnection))))
				.map(packet -> packet.getBuffer().readOptionalString()
			).orElse(null);
	}

	public static void sendProxyLoginSuccessPacket(@Nonnull PlayerNetworkProxyConnection playerConnection) {
		CloudWrapper.getInstance().getSocketComponent()
			.sendPacket(new PlayerApiPacket(PlayerActionType.PROXY_LOGIN_SUCCESS, buffer -> buffer.writeObject(playerConnection)));
	}

	public static void sendProxyServerConnectRequestPacket(@Nonnull PlayerNetworkProxyConnection playerConnection, @Nonnull ServiceInfo serviceInfo) {
		CloudWrapper.getInstance().getSocketComponent()
			.sendPacket(new PlayerApiPacket(PlayerActionType.PROXY_SERVER_CONNECT_REQUEST, buffer -> buffer.writeObject(playerConnection).writeObject(serviceInfo)));
	}

	public static void sendProxyServerSwitchPacket(@Nonnull PlayerNetworkProxyConnection playerConnection, @Nonnull ServiceInfo from, @Nonnull ServiceInfo to) {
		CloudWrapper.getInstance().getSocketComponent()
			.sendPacket(new PlayerApiPacket(PlayerActionType.PROXY_SERVER_SWITCH, buffer -> buffer.writeObject(playerConnection).writeObject(from).writeObject(to)));
	}

	public static void sendProxyDisconnectPacket(@Nonnull PlayerNetworkProxyConnection playerConnection) {
		CloudWrapper.getInstance().getSocketComponent()
			.sendPacket(new PlayerApiPacket(PlayerActionType.PROXY_DISCONNECT, buffer -> buffer.writeObject(playerConnection)));
	}

	public static void sendServerLoginRequestPacket(@Nonnull PlayerNetworkServerConnection playerConnection) {
		CloudWrapper.getInstance().getSocketComponent()
			.sendPacket(new PlayerApiPacket(PlayerActionType.SERVER_LOGIN_REQUEST, buffer -> buffer.writeObject(playerConnection)));
	}

	public static void sendServerLoginSuccessPacket(@Nonnull PlayerNetworkServerConnection playerConnection) {
		CloudWrapper.getInstance().getSocketComponent()
			.sendPacket(new PlayerApiPacket(PlayerActionType.SERVER_LOGIN_SUCCESS, buffer -> buffer.writeObject(playerConnection)));
	}

	public static void sendServerDisconnectPacket(@Nonnull PlayerNetworkServerConnection playerConnection) {
		CloudWrapper.getInstance().getSocketComponent()
			.sendPacket(new PlayerApiPacket(PlayerActionType.SERVER_DISCONNECT, buffer -> buffer.writeObject(playerConnection)));
	}

	private BridgeHelper() {}

}
