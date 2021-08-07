package net.anweisen.cloud.base.module.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ModuleConfig {

	private final String name, author, description, version, mainClass, website;
	private final String[] depends;
	private final ModuleCopyType copyType;
	private final Path jarFile;

	public ModuleConfig(@Nonnull String name, @Nonnull String author, @Nonnull String description, @Nonnull String version, @Nonnull String mainClass,
	                    @Nullable String website, @Nonnull String[] depends, @Nonnull ModuleCopyType copyType, @Nonnull Path jarFile) {
		this.name = name;
		this.author = author;
		this.description = description;
		this.version = version;
		this.jarFile = jarFile;
		this.mainClass = mainClass;
		this.website = website;
		this.depends = depends;
		this.copyType = copyType;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public String getAuthor() {
		return author;
	}

	@Nonnull
	public String getDescription() {
		return description;
	}

	@Nonnull
	public String getVersion() {
		return version;
	}

	@Nonnull
	public Path getJarFile() {
		return jarFile;
	}

	@Nonnull
	public String getMainClass() {
		return mainClass;
	}

	@Nullable
	public String getWebsite() {
		return website;
	}

	@Nonnull
	public String[] getDepends() {
		return depends;
	}

	@Nonnull
	public ModuleCopyType getCopyType() {
		return copyType;
	}

	@Nonnull
	public String getFullName() {
		return getName() + " v" + getVersion();
	}

	@Override
	public String toString() {
		return "ModuleConfig[" + getName() + " v" + getVersion() + " by " + getAuthor() + ": " + getJarFile().getFileName() + " copy=" + copyType + "]";
	}
}
