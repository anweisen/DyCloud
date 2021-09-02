package net.anweisen.cloud.driver.config.global;

import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
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
		getRawData().set("maxPlayers", maxPlayers);
		return this;
	}

	@Nonnull
	default GlobalConfig set(@Nonnull String path, @Nonnull Object value) {
		getRawData().set(path, value);
		return this;
	}

	default <T> T get(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return getRawData().get(path, classOfT);
	}

	void update();

	void fetch();

}
