package net.anweisen.cloud.driver.config.global;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.global.objects.CommandObject;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getGlobalConfig()
 */
public interface GlobalConfig {

	@Nonnull
	Document getRawData();

	@Nonnull
	GlobalConfig setRawData(@Nonnull Document rawData);

	default int getMaxPlayers() {
		return getRawData().getInt("maxPlayers");
	}

	@Nonnull
	default GlobalConfig setMaxPlayers(int maxPlayers) {
		return set("maxPlayers", maxPlayers);
	}

	default boolean getMaintenance() {
		return getRawData().getBoolean("maintenance");
	}

	@Nonnull
	default GlobalConfig setMaintenance(boolean maintenance) {
		return set("maintenance", maintenance);
	}

	@Nonnull
	default Collection<CommandObject> getIngameCommands() {
		return getRawData().getInstanceList("ingameCommands", CommandObject.class);
	}

	@Nonnull
	default GlobalConfig setIngameCommands(@Nonnull Collection<CommandObject> commands) {
		return set("ingameCommands", commands);
	}

	@Nonnull
	default GlobalConfig set(@Nonnull String path, @Nonnull Object value) {
		getRawData().set(path, value);
		return this;
	}

	default <T> T get(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return getRawData().getInstance(path, classOfT);
	}

	/**
	 * Synchronizes all global config instances with the properties of this instance
	 */
	void update();

	/**
	 * Reloads this global config instance
	 */
	void fetch();

}
