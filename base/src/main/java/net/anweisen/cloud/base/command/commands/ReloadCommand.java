package net.anweisen.cloud.base.command.commands;

import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.command.CommandScope;
import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.driver.player.permission.Permissions;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Command(
	name = "reload",
	scope = CommandScope.CONSOLE_AND_INGAME,
	permission = Permissions.CLOUD_COMMAND
)
public class ReloadCommand {

	@CommandPath("")
	public void onReloadCommand(@Nonnull CommandSender sender) throws Exception {
		CloudBase.getInstance().reload();
	}

}
