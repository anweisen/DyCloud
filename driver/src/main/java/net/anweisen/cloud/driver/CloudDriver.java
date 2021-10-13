package net.anweisen.cloud.driver;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.config.DriverConfig;
import net.anweisen.cloud.driver.config.global.GlobalConfig;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.event.EventManager;
import net.anweisen.cloud.driver.event.defaults.DefaultEventManager;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.node.NodeManager;
import net.anweisen.cloud.driver.player.PlayerManager;
import net.anweisen.cloud.driver.player.permission.PermissionManager;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.cloud.driver.translate.TranslationManager;
import net.anweisen.utilities.common.collection.NamedThreadFactory;
import net.anweisen.utilities.common.function.ExceptionallyRunnable;
import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.logging.handler.HandledLogger;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CloudDriver {

	public static final int DEFAULT_PORT = 3507;

	protected final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4, new NamedThreadFactory("CloudTask"));
	protected final EventManager eventManager = new DefaultEventManager();

	protected final long startupTime = System.currentTimeMillis();

	protected final DriverEnvironment environment;
	protected final HandledLogger logger;
	protected final Path tempDirectory;

	protected PermissionManager permissionManager;

	{
		Runtime.getRuntime().addShutdownHook(new Thread((ExceptionallyRunnable) this::shutdown, "ShutdownHook"));

		tempDirectory = Paths.get(System.getProperty("cloud.temp", ".temp"));
		FileUtils.createDirectory(tempDirectory);
		FileUtils.setTempDirectory(tempDirectory);
		FileUtils.setHiddenAttribute(tempDirectory, true);
	}

	protected CloudDriver(@Nonnull HandledLogger logger, @Nonnull DriverEnvironment environment) {
		this.logger = logger;
		this.environment = environment;
	}

	protected final void shutdownDriver() {

		FileUtils.delete(tempDirectory);

		executor.shutdownNow();

	}

	public abstract void shutdown() throws Exception;

	@Nonnull
	public Path getTempDirectory() {
		return tempDirectory;
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
	public abstract DriverConfig getConfig();

	@Nonnull
	public abstract GlobalConfig getGlobalConfig();

	@Nonnull
	public abstract SocketComponent getSocketComponent();

	@Nonnull
	public abstract DatabaseManager getDatabaseManager();

	@Nonnull
	public abstract ServiceConfigManager getServiceConfigManager();

	@Nonnull
	public abstract ServiceFactory getServiceFactory();

	@Nonnull
	public abstract ServiceManager getServiceManager();

	@Nonnull
	public abstract NodeManager getNodeManager();

	@Nonnull
	public abstract PlayerManager getPlayerManager();

	@Nonnull
	public abstract TranslationManager getTranslationManager();

	@Nonnull
	public PermissionManager getPermissionManager() {
		Preconditions.checkNotNull(permissionManager, "No Permission System available");
		return permissionManager;
	}

	public boolean hasPermissionManager() {
		return permissionManager != null;
	}

	public void setPermissionManager(@Nonnull PermissionManager manager) {
		this.permissionManager = manager;
		logger.trace("PermissionManager was set to {}", manager.getClass().getName());
	}

	@Nonnull
	public abstract String getComponentName();

	public long getStartupTime() {
		return startupTime;
	}

	public long getUpTime() {
		return System.currentTimeMillis() - startupTime;
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
