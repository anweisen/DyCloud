package net.anweisen.cloud.modules.bridge.bukkit;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.player.data.PlayerNetworkServerConnection;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BukkitBridgeHelper {

	private BukkitBridgeHelper() {}

	@Nonnull
	public static PlayerNetworkServerConnection createPlayerConnection(@Nonnull Player player) {
		return new PlayerNetworkServerConnection(
			player.getUniqueId(),
			player.getName(),
			HostAndPort.fromSocketAddressOrNull(player.getAddress()),
			BridgeHelper.getServiceInfo().getName(),
			BridgeHelper.getServiceInfo().getUniqueId()
		);
	}

}
