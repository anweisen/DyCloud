package net.anweisen.cloud.base.module;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface Module {

	@Nonnull
	ModuleController getController();

	@Nonnull
	static Module getProvidingModule(@Nonnull Class<?> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		if (!(loader instanceof ModuleClassLoader))
			throw new IllegalStateException(clazz.getName() + " was not loaded by a module (" + loader.getClass().getName() + ")");
		ModuleClassLoader moduleLoader = (ModuleClassLoader) loader;
		return moduleLoader.getModule();
	}

}
