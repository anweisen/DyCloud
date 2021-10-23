package net.anweisen.cloud.base.command.sender;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.base.command.sender.defaults.DefaultPlayerCommandSender;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerCommandSender extends CommandSender {

	@Nonnull
	@CheckReturnValue
	static PlayerCommandSender of(@Nonnull UUID playerUniqueId) {
		return of(Preconditions.checkNotNull(CloudDriver.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(playerUniqueId), "Player " + playerUniqueId + " is not online"));
	}

	@Nonnull
	@CheckReturnValue
	static PlayerCommandSender of(@Nonnull CloudPlayer player) {
		return new DefaultPlayerCommandSender(player);
	}

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	CloudPlayer getPlayer();

	@Nonnull
	PermissionPlayer getPermissionPlayer();

	@Nonnull
	PlayerExecutor getExecutor();

}
