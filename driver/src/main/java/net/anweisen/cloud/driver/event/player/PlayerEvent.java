package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.Event;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerManager;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class PlayerEvent implements Event {

	protected final CloudPlayer player;

	public PlayerEvent(@Nonnull CloudPlayer player) {
		this.player = player;
	}

	@Nonnull
	public CloudPlayer getPlayer() {
		return player;
	}

	@Nonnull
	public PlayerManager getPlayerManager() {
		return CloudDriver.getInstance().getPlayerManager();
	}
}