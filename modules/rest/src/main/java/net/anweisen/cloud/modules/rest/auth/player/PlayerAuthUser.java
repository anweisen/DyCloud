package net.anweisen.cloud.modules.rest.auth.player;

import net.anweisen.cloud.driver.network.http.auth.HttpAuthUser;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerAuthUser implements HttpAuthUser {

	private final PermissionPlayer player;

	public PlayerAuthUser(@Nonnull PermissionPlayer player) {
		this.player = player;
	}

	@Override
	public boolean hasPermission(@Nonnull String permission) {
		return player.hasPermission(permission);
	}
}
