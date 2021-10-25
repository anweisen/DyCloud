package net.anweisen.cloud.base.command.commands;

import net.anweisen.cloud.base.command.CommandScope;
import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceProperty;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Command(
	name = { "service", "ser" },
	permission = Permissions.CLOUD_COMMAND,
	scope = CommandScope.CONSOLE_AND_INGAME
)
public class ServiceCommand {

	@CommandPath("list")
	public void onListCommand(@Nonnull CommandSender sender) {
		Collection<ServiceInfo> services = CloudDriver.getInstance().getServiceManager().getServiceInfos();
		for (ServiceInfo service : services) {
			sender.sendTranslation("cloud.command.service.list.entry",
				service.getName(), service.getNodeName(), service.getState(), service.getAddress(),
				service.isConnected() ? "Connected" : "Not Connected",
				service.get(ServiceProperty.PHASE), service.get(ServiceProperty.ONLINE_PLAYERS)
			);
		}
		sender.sendTranslation("cloud.command.service.list.count", CloudDriver.getInstance().getPlayerManager().getOnlinePlayerCount());
	}

}
