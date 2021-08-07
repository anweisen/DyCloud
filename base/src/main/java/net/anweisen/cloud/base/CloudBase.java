package net.anweisen.cloud.base;

import net.anweisen.cloud.base.module.DefaultModuleManager;
import net.anweisen.cloud.base.module.ModuleManager;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CloudBase extends CloudDriver {

	protected final ModuleManager moduleManager = new DefaultModuleManager();

	protected final Console console;

	public CloudBase(@Nonnull ILogger logger, @Nonnull Console console, @Nonnull DriverEnvironment environment) {
		super(logger, environment);
		this.console = console;
	}

	protected final void shutdownBase() throws Exception {

		console.close();

	}

	@Nonnull
	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	private static CloudBase instance;

	public static CloudBase getInstance() {
		if (instance == null)
			instance = (CloudBase) CloudDriver.getInstance();

		return instance;
	}
}
