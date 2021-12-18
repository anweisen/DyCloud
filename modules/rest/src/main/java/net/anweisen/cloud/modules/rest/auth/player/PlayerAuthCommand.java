package net.anweisen.cloud.modules.rest.auth.player;

import net.anweisen.cloud.base.command.CommandScope;
import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.sender.PlayerCommandSender;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.utility.common.collection.IRandom;

import javax.annotation.Nonnull;
import java.util.Base64;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Command(
	name = "apitoken",
	scope = CommandScope.INGAME,
	permission = Permissions.REST_API_TOKEN_COMMAND
)
public class PlayerAuthCommand {

	@CommandPath("show")
	public void onShowCommand(@Nonnull PlayerCommandSender sender) {
		String playerToken = sender.getPlayer().getProperties().getString("rest-api-token");
		if (playerToken == null) {
			sender.sendTranslation("cloud.command.apitoken.show.none");
		} else {
			sender.sendTranslation("cloud.command.apitoken.show.success", playerToken);
		}
	}

	@CommandPath("generate")
	public void onGenerateCommand(@Nonnull PlayerCommandSender sender) {
		byte[] bytes = new byte[36];
		IRandom.singleton().nextBytes(bytes);
		String playerToken = Base64.getUrlEncoder().encodeToString(bytes);

		CloudPlayer player = sender.getPlayer();
		player.getProperties().set("rest-api-token", playerToken);
		player.save();

		sender.sendTranslation("cloud.command.apitoken.generate", playerToken);
	}

}
