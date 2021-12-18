package net.anweisen.cloud.modules.rest.auth.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.http.auth.HttpAuthHandler;
import net.anweisen.cloud.driver.network.http.auth.HttpAuthUser;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerAuthHandler implements HttpAuthHandler {

	@Nullable
	@Override
	public HttpAuthUser getAuthUser(@Nonnull String token) {
		String[] arguments = token.split(":");
		if (arguments.length != 2) return null;

		CloudOfflinePlayer player = CloudDriver.getInstance().getPlayerManager().getOfflinePlayerByName(arguments[0]);
		if (player == null) return null;
		String playerToken = player.getProperties().getString("rest-api-token");
		if (playerToken == null) return null;
		if (!playerToken.equals(arguments[1])) return null;

		return new PlayerAuthUser(player.getPermissionPlayer());
	}
}
