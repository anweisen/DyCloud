package net.anweisen.cloud.modules.bridge.bungee;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.player.data.PlayerNetworkProxyConnection;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BungeeBridgeHelper {

	private BungeeBridgeHelper() {}

	@Nonnull
	public static PlayerNetworkProxyConnection createPlayerConnection(@Nonnull PendingConnection connection) {
		return new PlayerNetworkProxyConnection(
			connection.getUniqueId(),
			connection.getName(),
			HostAndPort.fromSocketAddress(connection.getSocketAddress()),
			connection.getVersion(),
			connection.isOnlineMode(),
			connection.isLegacy()
		);
	}

	@Nullable
	public static ServerInfo getNextFallback(@Nonnull ProxiedPlayer player) {
		ServiceInfo service = BridgeHelper.getNextFallback(player.getUniqueId(), player::hasPermission);
		return service == null ? null : ProxyServer.getInstance().getServerInfo(service.getName()); // TODO warn if ServerInfo is null (should never happen, but if it does we want to know)
	}

	public static void registerServer(@Nonnull ServiceInfo serviceInfo) {
		CloudWrapper.getInstance().getLogger().debug("Registering server '{}' -> {}", serviceInfo.getName(), serviceInfo.getAddress());
		ProxyServer.getInstance().getServers().put(serviceInfo.getName(), ProxyServer.getInstance().constructServerInfo(
			serviceInfo.getName(),
			serviceInfo.getAddress().toInetSocketAddress(), "A MinecraftCloud service", false
		));
		BridgeHelper.cacheService(serviceInfo);
	}

	public static void unregisterServer(@Nonnull String name) {
		CloudWrapper.getInstance().getLogger().debug("Unregistering server '{}'", name);
		ProxyServer.getInstance().getServers().remove(name);
	}

}
