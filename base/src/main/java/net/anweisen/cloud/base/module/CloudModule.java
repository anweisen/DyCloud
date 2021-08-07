package net.anweisen.cloud.base.module;

import net.anweisen.cloud.base.module.config.ModuleConfig;
import net.anweisen.cloud.base.module.config.ModuleState;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.EventManager;
import net.anweisen.utilities.common.config.FileDocument;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CloudModule implements Module {

	FileDocument config;
	ModuleController controller;

	public CloudModule() {
	}

	protected void onLoad() throws Exception {}
	protected void onEnable() throws Exception {}
	protected void onDisable() throws Exception {}

	@Nonnull
	public final CloudDriver getDriver() {
		return CloudDriver.getInstance();
	}

	@Nonnull
	public ILogger getLogger() {
		return getDriver().getLogger();
	}

	@Nonnull
	public final EventManager getEventManager() {
		return getDriver().getEventManager();
	}

	public final void registerListeners(@Nonnull Object... listeners) {
		getEventManager().registerListeners(listeners);
	}

	@Nonnull
	@Override
	public final ModuleController getController() {
		if (controller == null) throw new IllegalStateException("Still in initialization");
		return controller;
	}

	@Nonnull
	public final Path getDataFolder() {
		return getController().getDataFolder();
	}

	@Nonnull
	public final ModuleConfig getModuleConfig() {
		return getController().getModuleConfig();
	}

	@Nonnull
	public final ModuleState getState() {
		return getController().getState();
	}

	@Nonnull
	@Override
	public FileDocument getConfig() {
		if (config == null)
			return reloadConfig();
		return config;
	}

	@Nonnull
	@Override
	public FileDocument reloadConfig() {
		synchronized (this) {
			return config = FileDocument.readJsonFile(this.getDataFolder().resolve("config.json").toFile());
		}
	}

	@Override
	public String toString() {
		return getModuleConfig().getFullName();
	}

}
