package net.anweisen.cloud.base.module;

import net.anweisen.cloud.base.module.config.ModuleConfig;
import net.anweisen.cloud.base.module.config.ModuleState;
import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface ModuleController {

	boolean isInitialized();

	@Nonnull
	Module getModule();

	void loadModule();

	void enableModule();

	void disableModule();

	void unregisterModule();

	@Nonnull
	ModuleManager getManager();

	@Nonnull
	Path getJarFile();

	@Nonnull
	Path getDataFolder();

	@Nonnull
	FileDocument getConfig();

	@Nonnull
	FileDocument reloadConfig();

	boolean isEnabled();

	@Nonnull
	ModuleState getState();

	@Nonnull
	ModuleConfig getModuleConfig();

	@Nonnull
	ModuleClassLoader getClassLoader();

}
