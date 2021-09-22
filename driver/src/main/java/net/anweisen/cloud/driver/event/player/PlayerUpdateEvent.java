package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;

/**
 * Called when a CloudPlayer was updated remotely.
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudPlayer#update()
 */
public class PlayerUpdateEvent extends PlayerEvent {

	public PlayerUpdateEvent(@Nonnull CloudPlayer player) {
		super(player);
	}
}
