package net.anweisen.cloud.base.module.config;

import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ModuleConfig implements SerializableObject {

	private String name, author, description, version, mainClass, website;
	private String[] depends;
	private ModuleCopyType copyType;
	private ModuleEnvironment environment;

	private ModuleConfig() {
	}

	public ModuleConfig(@Nonnull String name, @Nonnull String author, @Nonnull String description, @Nonnull String version, @Nonnull String mainClass,
	                    @Nullable String website, @Nonnull String[] depends, @Nonnull ModuleCopyType copyType, @Nonnull ModuleEnvironment environment) {
		this.name = name;
		this.author = author;
		this.description = description;
		this.version = version;
		this.mainClass = mainClass;
		this.website = website;
		this.depends = depends;
		this.copyType = copyType;
		this.environment = environment;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeString(name);
		buffer.writeString(author);
		buffer.writeString(description);
		buffer.writeString(version);
		buffer.writeString(mainClass);
		buffer.writeOptionalString(website);
		buffer.writeStringArray(depends);
		buffer.writeEnum(copyType);
		buffer.writeEnum(environment);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		name = buffer.readString();
		author = buffer.readString();
		description = buffer.readString();
		version = buffer.readString();
		mainClass = buffer.readString();
		website = buffer.readOptionalString();
		depends = buffer.readStringArray();
		copyType = buffer.readEnum(ModuleCopyType.class);
		environment = buffer.readEnum(ModuleEnvironment.class);
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
	public ModuleEnvironment getEnvironment() {
		return environment;
	}

	@Nonnull
	public String getFullName() {
		return name + " v" + version + " by " + author;
	}

	@Override
	public String toString() {
		return "ModuleConfig[" + name + " v" + version + " by " + author + " copy=" + copyType + " environment=" + environment + "]";
	}
}
