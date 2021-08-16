package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerApiPacket.PlayerActionType;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Triggered by {@link PlayerActionType#SERVER_DISCONNECT}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerServerDisconnectEvent extends PlayerServerEvent {

	public PlayerServerDisconnectEvent(@Nullable CloudPlayer player, @Nonnull ServiceInfo service) {
		super(player, service);
	}

	/**
	 * @return the online player or {@code null} if the player disconnected from the proxy and got disconnected from the server for that reason
	 */
	@Nullable
	@Override
	public CloudPlayer getPlayer() {
		return super.getPlayer();
	}
}
