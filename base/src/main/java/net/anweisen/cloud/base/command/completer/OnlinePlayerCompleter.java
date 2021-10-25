package net.anweisen.cloud.base.command.completer;

import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.driver.CloudDriver;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class OnlinePlayerCompleter implements CommandCompleter {

	@Nonnull
	@Override
	public Collection<String> complete(@Nonnull CommandSender sender, @Nonnull String message, @Nonnull String argument) {
		return CloudDriver.getInstance().getPlayerManager().getOnlinePlayerNames();
	}

}
