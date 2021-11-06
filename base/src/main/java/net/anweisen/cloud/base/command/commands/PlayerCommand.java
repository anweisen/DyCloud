package net.anweisen.cloud.base.command.commands;

import net.anweisen.cloud.base.command.CommandScope;
import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandArgument;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.completer.OnlinePlayerCompleter;
import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerManager;
import net.anweisen.cloud.driver.player.permission.Permissions;

import javax.annotation.Nonnull;
import java.util.UUID;

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

	@CommandPath("kick <player> <reason>")
	public void onKickCommand(@Nonnull CommandSender sender,
	                          @CommandArgument(value = "player", completer = OnlinePlayerCompleter.class) String playerName,
	                          @CommandArgument(value = "reason", optional = true) String reason) {
		CloudPlayer player = online(sender, playerName);
		if (player == null) return;

		player.getExecutor().disconnect(reason);
		sender.sendTranslation("cloud.command.player.kick");
	}

	@CommandPath("delete <player>")
	public void onDeleteCommand(@Nonnull CommandSender sender,
	                            @CommandArgument(value = "player") String playerName)  {
		CloudOfflinePlayer player = offline(sender, playerName);
		if (player == null) return;

		CloudDriver.getInstance().getPlayerManager().deleteOfflinePlayer(player);
		sender.sendTranslation("cloud.command.player.delete");
	}

	private CloudPlayer online(@Nonnull CommandSender sender, @Nonnull String input) {
		PlayerManager manager = CloudDriver.getInstance().getPlayerManager();
		CloudPlayer player = manager.getOnlinePlayerByName(input);
		if (player != null) return player;

		try {
			player = manager.getOnlinePlayerByUniqueId(UUID.fromString(input));
			if (player != null) return player;
		} catch (Exception ex) {
			// Input is not a valid uuid
		}

		sender.sendTranslation("cloud.player.offline");
		return null;
	}

	private CloudOfflinePlayer offline(@Nonnull CommandSender sender, @Nonnull String input) {
		PlayerManager manager = CloudDriver.getInstance().getPlayerManager();
		CloudOfflinePlayer player = manager.getOfflinePlayerByName(input);
		if (player != null) return player;

		try {
			player = manager.getOfflinePlayerByUniqueId(UUID.fromString(input));
			if (player != null) return player;
		} catch (Exception ex) {
			// Input is not a valid uuid
		}

		sender.sendTranslation("cloud.player.unregistered");
		return null;
	}

}
