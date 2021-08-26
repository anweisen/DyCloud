package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventType;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * Triggered by {@link PlayerEventType#SERVER_LOGIN_SUCCESS}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerServerLoginSuccessEvent extends PlayerServerEvent {

	public PlayerServerLoginSuccessEvent(@Nonnull CloudPlayer player, @Nonnull ServiceInfo service) {
		super(player, service);
	}
}
