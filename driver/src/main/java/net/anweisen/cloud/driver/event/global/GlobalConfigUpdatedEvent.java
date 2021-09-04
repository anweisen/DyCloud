package net.anweisen.cloud.driver.event.global;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.global.GlobalConfig;
import net.anweisen.cloud.driver.event.Event;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class GlobalConfigUpdatedEvent implements Event {

	@Nonnull
	public GlobalConfig getConfig() {
		return CloudDriver.getInstance().getGlobalConfig();
	}
}