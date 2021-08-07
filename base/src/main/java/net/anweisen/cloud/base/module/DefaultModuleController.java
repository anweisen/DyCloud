package net.anweisen.cloud.base.module;

import net.anweisen.cloud.base.module.config.ModuleConfig;
import net.anweisen.cloud.base.module.config.ModuleCopyType;
import net.anweisen.cloud.base.module.config.ModuleState;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.utilities.common.config.Document;

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
	private ModuleClassLoader classLoader;
	private ModuleConfig moduleConfig;
	private CloudModule module;

	private ModuleState state = ModuleState.DISABLED;

	public DefaultModuleController(@Nonnull ModuleManager manager, @Nonnull Path jarFile) {
		this.manager = manager;
		this.jarFile = jarFile;
	}

	public void initConfig() throws Exception {

		URL url = jarFile.toUri().toURL();

		classLoader = new ModuleClassLoader(url, this.getClass().getClassLoader());

		InputStream input = classLoader.getResourceAsStream(CONFIG_RESOURCE);
		if (input == null) throw new IllegalArgumentException("No such resource " + CONFIG_RESOURCE);

		InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		Document document;

		try {
			document = Document.parseJson(reader);
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
			document.getStringArray("depends"),
			document.getEnum("copy", ModuleCopyType.NONE),
			jarFile
		);
	}

	public void initModule() throws Exception {

		dataFolder = manager.getModulesFolder().resolve(moduleConfig.getName());

		Class<?> mainClass = classLoader.loadClass(moduleConfig.getMainClass());
		Constructor<?> constructor = mainClass.getDeclaredConstructor();
		Object instance = constructor.newInstance();
		if (!(instance instanceof CloudModule)) throw new IllegalArgumentException("Main class (" + moduleConfig.getMainClass() + ") does not extend " + CloudModule.class.getName());

		module = (CloudModule) instance;
		module.controller = this;

		classLoader.module = module;

	}

	@Nonnull
	@Override
	public ModuleController loadModule() {
		synchronized (this) {
			if (state != ModuleState.DISABLED) return this; // Must be disabled first

			info("Module {} by {} is being loaded..", module, moduleConfig.getAuthor());

			try {
				module.onLoad();
				state = ModuleState.LOADED;
			} catch (Throwable ex) {
				error("An error occurred while loading module {}", module, ex);
				disableModule();
			}

			return this;
		}
	}

	@Nonnull
	@Override
	public ModuleController enableModule() {
		synchronized (this) {
			if (state != ModuleState.LOADED) return this; // Must be loaded first

			info("Module {} by {} is being enabled..", module, moduleConfig.getAuthor());

			try {
				module.onEnable();
				state = ModuleState.ENABLED;
			} catch (Throwable ex) {
				error("An error occurred while enabling module {}", module, ex);
				disableModule();
			}

			return this;
		}
	}

	@Nonnull
	@Override
	public ModuleController disableModule() {
		synchronized (this) {
			info("Module {} by {} is being disabled..", module, moduleConfig.getAuthor());
			CloudDriver.getInstance().getEventManager().unregisterListeners(classLoader);

			try {
				module.onDisable();
			} catch (Throwable ex) {
				error("An error occurred while disabling module {}", module, ex);
			}

			state = ModuleState.DISABLED;

			return this;
		}
	}

	@Nonnull
	@Override
	public ModuleState getState() {
		return state;
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
