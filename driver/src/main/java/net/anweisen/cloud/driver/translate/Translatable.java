package net.anweisen.cloud.driver.translate;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface Translatable {

	@Nonnull
	@CheckReturnValue
	static Translatable of(@Nonnull String name) {
		return CloudDriver.getInstance().getTranslationManager().getTranslatable(name);
	}

	@Nonnull
	String getName();

	@Nonnull
	TranslatedValue translateDefault();

	@Nonnull
	TranslatedValue translate(@Nonnull String language);

	@Nonnull
	TranslatedValue translate(@Nonnull UUID playerUniqueId);

	@Nonnull
	TranslatedValue translate(@Nonnull CloudOfflinePlayer player);

}
