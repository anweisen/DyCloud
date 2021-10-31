package net.anweisen.cloud.modules.hub.command;

import net.anweisen.cloud.base.command.CommandScope;
import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.sender.PlayerCommandSender;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Command(
	name = { "hub", "lobby", "leave", "l" },
	scope = CommandScope.INGAME
)
public class HubCommand {

	@CommandPath("")
	public void onHubCommand(@Nonnull PlayerCommandSender sender) {

		sender.getExecutor().connectFallback();

	}

}
