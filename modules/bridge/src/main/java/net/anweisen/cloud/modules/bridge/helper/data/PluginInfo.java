package net.anweisen.cloud.modules.bridge.helper.data;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PluginInfo {

	private String name;
	private String[] author;
	private String version;
	private String mainClass;
	private String description;

	public PluginInfo(String name, String[] author, String version, String mainClass, String description) {
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

	@Nonnull
	public String getDescription() {
		return description;
	}
}
