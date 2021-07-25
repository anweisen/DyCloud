package net.anweisen.cloud.driver.console;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface LoggingApiUser extends net.anweisen.utilities.common.logging.LoggingApiUser {

	@Nonnull
	default ILogger getLogger() {
		return CloudDriver.getInstance().getLogger();
	}

}
