package net.anweisen.cloud.modules.hub;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.modules.hub.command.HubCommand;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudHubCommandModule extends CloudModule {

	@Override
	protected void onLoad() {
		registerCommands(new HubCommand());
	}
}
