package net.anweisen.cloud.base.command.commands;

import net.anweisen.cloud.base.command.CommandScope;
import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandArgument;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.completer.CommandCompleter;
import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.permission.Permissions;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Command(
	name = { "player", "players", "p" },
	permission = Permissions.CLOUD_COMMAND,
	scope = CommandScope.CONSOLE_AND_INGAME
)
public class PlayerCommand {

	@CommandPath("list")
	public void onListCommand(@Nonnull CommandSender sender) {
		for (CloudPlayer player : CloudDriver.getInstance().getPlayerManager().getOnlinePlayers())
			sender.sendTranslation("cloud.command.player.list.entry", player.getUniqueId(), player.getName());
		sender.sendTranslation("cloud.command.player.list.count", CloudDriver.getInstance().getPlayerManager().getOnlinePlayerCount());
	}

	@CommandPath("kick <player>")
	public void onKickCommand(@Nonnull CommandSender sender, @CommandArgument(value = "player", completer = CommandCompleter.ONLINE_PLAYER) String playerName) {
		CloudPlayer player = CloudDriver.getInstance().getPlayerManager().getOnlinePlayerByName(playerName);

		if (player == null) {

			return;
		}
	}

}
