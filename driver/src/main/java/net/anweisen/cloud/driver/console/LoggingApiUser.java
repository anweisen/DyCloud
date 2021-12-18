package net.anweisen.cloud.driver.console;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.utility.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface LoggingApiUser extends net.anweisen.utility.common.logging.LoggingApiUser {

	@Nonnull
	@Override
	default ILogger getTargetLogger() {
		return CloudDriver.getInstance().getLogger();
	}

}
