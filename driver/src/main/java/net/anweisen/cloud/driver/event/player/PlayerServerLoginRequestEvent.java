package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerApiPacket.PlayerActionType;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * Triggered by {@link PlayerActionType#SERVER_LOGIN_REQUEST}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerServerLoginRequestEvent extends PlayerServerEvent {

	public PlayerServerLoginRequestEvent(@Nonnull CloudPlayer player, @Nonnull ServiceInfo service) {
		super(player, service);
	}
}
