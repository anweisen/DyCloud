package net.anweisen.cloud.base.module;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface ModuleManager {

	void resolveModules();

	void loadModules();

	void enableModules();

	void disableModules();

	@Nonnull
	Path getModulesFolder();

	@Nonnull
	Collection<ModuleController> getModules();

}
