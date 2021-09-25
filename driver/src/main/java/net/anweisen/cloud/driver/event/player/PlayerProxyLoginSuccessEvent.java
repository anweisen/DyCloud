package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventPayload;
import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;

/**
 * Called when a player is successfully connected to a proxy
 *
 * Triggered by {@link PlayerEventPayload#PROXY_LOGIN_SUCCESS}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerProxyLoginSuccessEvent extends PlayerProxyEvent {

	public PlayerProxyLoginSuccessEvent(@Nonnull CloudPlayer player) {
		super(player);
	}

}
