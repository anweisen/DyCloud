package net.anweisen.cloud.driver;

import com.google.common.base.Preconditions;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CloudDriver {

	protected final ILogger logger;
	protected final DriverEnvironment environment;

	public CloudDriver(@Nonnull ILogger logger, @Nonnull DriverEnvironment environment) {
		this.logger = logger;
		this.environment = environment;
	}

	@Nonnull
	public ILogger getLogger() {
		return logger;
	}

	@Nonnull
	public DriverEnvironment getEnvironment() {
		return environment;
	}

	private static CloudDriver instance;

	public static CloudDriver getInstance() {
		return instance;
	}

	protected static void setInstance(@Nonnull CloudDriver instance) {
		Preconditions.checkNotNull(instance);
		CloudDriver.instance = instance;
	}

}
