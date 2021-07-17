package net.anweisen.cloud.base;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CloudBase extends CloudDriver {

	protected final Console console;

	public CloudBase(@Nonnull ILogger logger, @Nonnull Console console, @Nonnull DriverEnvironment environment) {
		super(logger, environment);
		this.console = console;
	}

	private static CloudBase instance;

	public static CloudBase getInstance() {
		if (instance == null)
			instance = (CloudBase) CloudDriver.getInstance();

		return instance;
	}
}
