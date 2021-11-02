package net.anweisen.cloud.base.command.commands;

import net.anweisen.cloud.base.command.CommandScope;
import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.cloud.driver.service.specific.*;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Predicate;

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
				service.getName(),
				service.getNodeName(),
				service.getAddress(),
				service.getState(),
				service.getControlState(),
				service.isConnected() ? "Connected" : "Not Connected",
				service.get(ServiceProperty.PHASE),
				service.get(ServiceProperty.ONLINE_PLAYERS),
				service.get(ServiceProperty.MAX_PLAYERS)
			);
		}

		sender.sendTranslation("cloud.command.service.list.count",
			services.size(),
			count(services, service -> service.getState() == ServiceState.RUNNING),
			count(services, ServiceInfo::isReady),
			count(services, ServicePropertyHelper::isIngame),
			count(services, service -> service.getControlState() == ServiceControlState.STARTING)
		);
	}

	private int count(@Nonnull Collection<ServiceInfo> services, @Nonnull Predicate<ServiceInfo> filter) {
		int number = 0;
		for (ServiceInfo service : services) {
			if (filter.test(service))
				number++;
		}
		return number;
	}
}
