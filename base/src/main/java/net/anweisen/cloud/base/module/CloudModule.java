package net.anweisen.cloud.base.module;

import net.anweisen.cloud.base.module.config.ModuleConfig;
import net.anweisen.cloud.base.module.config.ModuleState;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.global.GlobalConfig;
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

	ModuleController controller;

	public CloudModule() {
	}

	protected void onLoad() {}
	protected void onEnable() {}
	protected void onDisable() {}

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
	public GlobalConfig getGlobalConfig() {
		return getDriver().getGlobalConfig();
	}

	@Nonnull
	public FileDocument getConfig() {
		return controller.getConfig();
	}

	@Nonnull
	public FileDocument reloadConfig() {
		return controller.reloadConfig();
	}

	protected boolean getEnabled(boolean defaultValue) {
		FileDocument config = getConfig();
		if (!config.contains("enabled")) {
			config.set("enabled", defaultValue).save();
		}

		boolean enabled = config.getBoolean("enabled");
		getLogger().debug("'{}' Status: enabled={}", getModuleConfig().getFullName(), enabled);
		return enabled;
	}

	public boolean isEnabled() {
		return !getConfig().contains("enabled") || getConfig().getBoolean("enabled");
	}

	@Override
	public String toString() {
		return getModuleConfig().getFullName() + " (" + getModuleConfig().getJarFile().getFileName() + ")";
	}

}
