package net.anweisen.cloud.base.command.sender;

import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerCommandSender extends CommandSender {

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	CloudPlayer getPlayer();

	@Nonnull
	PermissionPlayer getPermissionPlayer();

	@Nonnull
	PlayerExecutor getExecutor();

}
