package net.anweisen.cloud.base.command.completer;

import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.driver.service.specific.ServiceEnvironment;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceEnvironmentCompleter implements CommandCompleter {

	@Nonnull
	@Override
	public Collection<String> complete(@Nonnull CommandSender sender, @Nonnull String message, @Nonnull String argument) {
		return Arrays.stream(ServiceEnvironment.values()).map(ServiceEnvironment::name).collect(Collectors.toList());
	}
}
