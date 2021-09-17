package net.anweisen.cloud.driver.service.specific.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#PLUGINS
 */
public final class PluginInfo {

	private String name;
	private String[] author;
	private String version;
	private String mainClass;
	private String description;

	private PluginInfo() {
	}

	public PluginInfo(@Nonnull String name, @Nonnull String[] author, @Nonnull String version, @Nonnull String mainClass, @Nullable String description) {
		this.name = name;
		this.author = author;
		this.version = version;
		this.mainClass = mainClass;
		this.description = description;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public String getVersion() {
		return version;
	}

	@Nonnull
	public String[] getAuthor() {
		return author;
	}

	@Nonnull
	public String getMainClass() {
		return mainClass;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return name + " v" + version;
	}
}
