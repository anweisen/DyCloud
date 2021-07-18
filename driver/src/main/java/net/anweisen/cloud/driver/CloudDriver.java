package net.anweisen.cloud.driver;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CloudDriver {

	protected final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
	protected final EventManager eventManager = new DefaultEventManager();

	protected final ILogger logger;
	protected final DriverEnvironment environment;

	public CloudDriver(@Nonnull ILogger logger, @Nonnull DriverEnvironment environment) {
		this.logger = logger;
		this.environment = environment;
	}

	@Nonnull
	public ScheduledExecutorService getExecutor() {
		return executor;
	}

	@Nonnull
	public EventManager getEventManager() {
		return eventManager;
	}

	@Nonnull
	public ILogger getLogger() {
		return logger;
	}

	@Nonnull
	public DriverEnvironment getEnvironment() {
		return environment;
	}

	@Nonnull
	public abstract DatabaseManager getDatabaseManager();

	private static CloudDriver instance;

	public static CloudDriver getInstance() {
		return instance;
	}

	protected static void setInstance(@Nonnull CloudDriver instance) {
		Preconditions.checkNotNull(instance);
		CloudDriver.instance = instance;
	}

}
