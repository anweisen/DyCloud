package net.anweisen.cloud.driver.event.permission;

import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class PermissionPlayerEvent extends PermissionEvent {

	protected final PermissionPlayer player;

	public PermissionPlayerEvent(@Nonnull PermissionPlayer player) {
		this.player = player;
	}

	@Nonnull
	public PermissionPlayer getPlayer() {
		return player;
	}
}
