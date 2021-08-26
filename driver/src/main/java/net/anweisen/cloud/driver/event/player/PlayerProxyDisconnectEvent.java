package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventType;
import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;

/**
 * Called when a player is being disconnected from a proxy.
 * The player is no longer online at the time this event is called.
 *
 * Triggered by {@link PlayerEventType#PROXY_DISCONNECT}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerProxyDisconnectEvent extends PlayerProxyEvent {

	public PlayerProxyDisconnectEvent(@Nonnull CloudPlayer player) {
		super(player);
	}

}
