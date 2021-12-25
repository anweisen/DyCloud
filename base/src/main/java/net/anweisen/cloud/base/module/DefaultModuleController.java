package net.anweisen.cloud.base.module;

import net.anweisen.cloud.base.module.config.ModuleConfig;
import net.anweisen.cloud.base.module.config.ModuleCopyType;
import net.anweisen.cloud.base.module.config.ModuleEnvironment;
import net.anweisen.cloud.base.module.config.ModuleState;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;
import net.anweisen.utility.document.wrapped.StorableDocument;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultModuleController implements ModuleController, LoggingApiUser {

	protected static final String CONFIG_RESOURCE = "module.json";

	private final ModuleManager manager;
	private final Path jarFile;

	private Path dataFolder;
	private StorableDocument config;
	private ModuleClassLoader classLoader;
	private ModuleConfig moduleConfig;
	private CloudModule module;

	private ModuleState state = ModuleState.DISABLED;

	public DefaultModuleController(@Nonnull ModuleManager manager, @Nonnull Path jarFile) {
		this.manager = manager;
		this.jarFile = jarFile;
	}

	@Override
	public boolean isInitialized() {
		return module != null;
	}

	public void initConfig() throws Exception {

		URL url = jarFile.toUri().toURL();

		classLoader = new ModuleClassLoader(url, this.getClass().getClassLoader());

		InputStream input = classLoader.getResourceAsStream(CONFIG_RESOURCE);
		if (input == null) throw new IllegalArgumentException("No such resource " + CONFIG_RESOURCE);

		InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		Document document;

		try {
			document = Documents.newJsonDocument(reader);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to parse module config", ex);
		}

		if (!document.contains("name")) throw new IllegalArgumentException("Missing property 'name'");
		if (!document.contains("version")) throw new IllegalArgumentException("Missing property 'version'");
		if (!document.contains("author")) throw new IllegalArgumentException("Missing property 'author'");
		if (!document.contains("main")) throw new IllegalArgumentException("Missing property 'main'");

		moduleConfig = new ModuleConfig(
			document.getString("name"),
			document.getString("author"),
			document.getString("description", ""),
			document.getString("version"),
			document.getString("main"),
			document.getString("website", ""),
			document.getBundle("depends").toStrings().toArray(new String[0]), // TODO ugly
			document.getEnum("copy", ModuleCopyType.NONE),
			document.getEnum("environment", ModuleEnvironment.ALL)
		);

		dataFolder = manager.getModulesDirectory().resolve(moduleConfig.getName());
	}

	public void initModule() throws Exception {

		Class<?> mainClass = classLoader.loadClass(moduleConfig.getMainClass());
		Constructor<?> constructor = mainClass.getDeclaredConstructor();
		Object instance = constructor.newInstance();
		if (!(instance instanceof CloudModule))
			throw new IllegalArgumentException("Main class (" + moduleConfig.getMainClass() + ") does not extend " + CloudModule.class.getName());

		module = (CloudModule) instance;
		module.controller = this;

		classLoader.setModule(module);

	}

	@Override
	public void loadModule() {
		synchronized (this) {
			if (module == null) return; // was never initialized
			if (state != ModuleState.DISABLED) return; // must be disabled first

			info("Module {} is being loaded..", module);

			try {
				module.onLoad();
				state = ModuleState.LOADED;
			} catch (Throwable ex) {
				error("An error occurred while loading module {}", module, ex);
				disableModule();
			}
		}
	}

	@Override
	public void enableModule() {
		synchronized (this) {
			if (module == null) return; // was never initialized
			if (state != ModuleState.LOADED) return; // must be loaded first

			info("Module {} is being enabled..", module);

			try {
				module.onEnable();
				state = ModuleState.ENABLED;
			} catch (Throwable ex) {
				error("An error occurred while enabling module {}", module, ex);
				disableModule();
			}
		}
	}

	@Override
	public void disableModule() {
		synchronized (this) {
			if (module == null) return; // Was never initialized
			if (state == ModuleState.DISABLED) return; // Is already disabled

			info("Module {} is being disabled..", module);
			CloudDriver.getInstance().getEventManager().unregisterListeners(classLoader);

			try {
				module.onDisable();
			} catch (Throwable ex) {
				error("An error occurred while disabling module {}", module, ex);
			}

			state = ModuleState.DISABLED;
		}
	}

	@Override
	public void unregisterModule() {
		synchronized (this) {

			try {
				classLoader.close();
			} catch (Exception ex) {
				error("Unable to close classloader", ex);
			}

			state = ModuleState.UNREGISTERED;
		}
	}

	@Nonnull
	@Override
	public StorableDocument getConfig() {
		if (config == null)
			return reloadConfig();
		return config;
	}

	@Nonnull
	@Override
	public StorableDocument reloadConfig() {
		synchronized (this) {
			return config = Documents.newStorableJsonDocumentUnchecked(this.getDataFolder().resolve("config.json"));
		}
	}

	@Override
	public boolean isEnabled() {
		return module == null ? !getConfig().contains("enabled") || getConfig().getBoolean("enabled") : module.isEnabled();
	}

	@Nonnull
	@Override
	public ModuleState getState() {
		return state;
	}

	@Nonnull
	@Override
	public Path getJarFile() {
		return jarFile;
	}

	@Nonnull
	@Override
	public Path getDataFolder() {
		return dataFolder;
	}

	@Nonnull
	@Override
	public ModuleManager getManager() {
		return manager;
	}

	@Nonnull
	@Override
	public ModuleConfig getModuleConfig() {
		return moduleConfig;
	}

	@Nonnull
	@Override
	public CloudModule getModule() {
		return module;
	}

	@Nonnull
	@Override
	public ModuleClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public String toString() {
		return "ModuleController[file=" + jarFile.getFileName() + "]";
	}
}
