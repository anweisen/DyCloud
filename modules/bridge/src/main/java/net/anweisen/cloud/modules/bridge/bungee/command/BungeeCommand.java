package net.anweisen.cloud.modules.bridge.bungee.command;

import net.anweisen.cloud.driver.config.global.objects.CommandObject;
import net.anweisen.cloud.modules.bridge.helper.BridgeNetworkingHelper;
import net.anweisen.utilities.common.misc.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCommand extends Command implements TabExecutor {

	public BungeeCommand(@Nonnull String name, @Nonnull Collection<CommandObject> commands) {
		super(name);
	}

	@Override
	public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
		if (!(sender instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer) sender;
		BridgeNetworkingHelper.sendCommandExecutePacket(player.getUniqueId(), getName() + (args.length == 0 ? "" : " ") + StringUtils.getArrayAsString(args, " "));
	}

	@Override
	public Iterable<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
		if (!(sender instanceof ProxiedPlayer)) return null;
		ProxiedPlayer player = (ProxiedPlayer) sender;
		return BridgeNetworkingHelper.sendCommandCompletePacket(player.getUniqueId(), getName() + (args.length == 0 ? "" : " ") + StringUtils.getArrayAsString(args, " "));
	}
}
