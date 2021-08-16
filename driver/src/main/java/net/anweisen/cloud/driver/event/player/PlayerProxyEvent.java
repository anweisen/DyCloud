package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class PlayerProxyEvent extends PlayerEvent {

	public PlayerProxyEvent(@Nonnull CloudPlayer player) {
		super(player);
	}
}
