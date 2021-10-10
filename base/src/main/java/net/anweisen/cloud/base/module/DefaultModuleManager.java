package net.anweisen.cloud.base.module;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultModuleManager implements ModuleManager, LoggingApiUser {

	private List<DefaultModuleController> modules = Collections.emptyList();
	private Path directory;

	@Nonnull
	public Path getModulesDirectory() {
		return directory;
	}

	@Override
	public void setModulesDirectory(@Nonnull Path directory) {
		FileUtils.createDirectory(directory);
		this.directory = directory;
	}

	@Override
	public synchronized void unregisterModules() {
		for (DefaultModuleController module : modules) {
			try {
				module.unregisterModule();
			} catch (Throwable ex) {
				error("An error occurred while closing class loader", ex);
			}
		}
		modules.clear();
	}

	@Override
	public synchronized void resolveModules() {
		unregisterModules();
		info("Resolving modules..");
		FileUtils.createDirectory(directory);

		List<DefaultModuleController> modules = new CopyOnWriteArrayList<>();

		// Resolve modules and load configs
		for (Path file : FileUtils.list(directory).filter(path -> path.toString().endsWith(".jar")).collect(Collectors.toList())) {
			try {
				info("Resolving module {}..", file.getFileName());
				DefaultModuleController module = new DefaultModuleController(this, file);
				module.initConfig();

				modules.add(module);
			} catch (Throwable ex) {
				error("Could not resolve module {}", FileUtils.getRealFileName(file), ex);
			}
		}

		// Check if the depends are existing
		for (DefaultModuleController module : modules) {
			for (String depend : module.getModuleConfig().getDepends()) {
				if (hasModule(modules, depend)) continue;

				modules.remove(module);
				error("Could not find required depend '{}' for module {}", depend, module.getModuleConfig());
			}
		}

		// Order the modules by depends
		modules.sort((module1, module2) -> {

			// If this module requires the other module, load this after the other
			if (arrayContains(module1.getModuleConfig().getDepends(), module2.getModuleConfig().getName()))
				return -1;

			// If the other module requires this module, load the other after this
			if (arrayContains(module2.getModuleConfig().getDepends(), module1.getModuleConfig().getName()))
				return 1;

			return 0;
		});

		// Init modules
		for (DefaultModuleController module : modules) {
			try {
				if (!module.getModuleConfig().getEnvironment().applies(CloudDriver.getInstance().getEnvironment())) {
					info("Skipping initialization of {} (ModuleEnvironment.{}, DriverEnvironment.{})",
						 module, module.getModuleConfig().getEnvironment(), CloudDriver.getInstance().getEnvironment());
					continue;
				}

				module.initModule();
			} catch (Throwable ex) {
				modules.remove(module);
				error("An error occurred while initializing {}", module.getModuleConfig(), ex);
			}
		}

		this.modules = modules;
	}

	private boolean hasModule(@Nonnull Collection<DefaultModuleController> modules, @Nonnull String depend) {
		for (DefaultModuleController module : modules) {
			if (module.getModuleConfig().getName().equals(depend))
				return true;
		}
		return false;
	}

	private <T> boolean arrayContains(@Nonnull T[] array, T subject) {
		for (T t : array) {
			if (t.equals(subject))
				return true;
		}
		return false;
	}

	@Override
	public synchronized void loadModules() {
		for (ModuleController module : modules) {
			module.loadModule();
		}
	}

	@Override
	public synchronized void enableModules() {
		for (ModuleController module : modules) {
			module.enableModule();
		}
	}

	@Override
	public synchronized void disableModules() {
		for (ModuleController module : modules) {
			module.disableModule();
		}
	}

	@Nonnull
	@Override
	public List<ModuleController> getModules() {
		return Collections.unmodifiableList(modules);
	}

}
