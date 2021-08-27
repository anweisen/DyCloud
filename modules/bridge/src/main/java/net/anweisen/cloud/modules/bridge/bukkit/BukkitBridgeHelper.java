package net.anweisen.cloud.modules.bridge.bukkit;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.player.data.PlayerServerConnectionData;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BukkitBridgeHelper {

	private BukkitBridgeHelper() {}

	@Nonnull
	public static PlayerServerConnectionData createPlayerConnection(@Nonnull Player player) {
		return new PlayerServerConnectionData(
			player.getUniqueId(),
			player.getName(),
			HostAndPort.fromSocketAddressOrNull(player.getAddress())
		);
	}

}
