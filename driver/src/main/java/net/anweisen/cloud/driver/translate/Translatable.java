package net.anweisen.cloud.driver.translate;

import com.google.common.base.Preconditions;
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
	default TranslatedValue translateDefault() {
		return translate(CloudDriver.getInstance().getTranslationManager().findDefaultLanguage());
	}

	@Nonnull
	default TranslatedValue translate(@Nonnull String language) {
		return translate(Preconditions.checkNotNull(CloudDriver.getInstance().getTranslationManager().getLanguage(language), "Unknown language '" + language + "'"));
	}

	@Nonnull
	default TranslatedValue translate(@Nonnull UUID playerUniqueId) {
		CloudOfflinePlayer player = CloudDriver.getInstance().getPlayerManager().getOfflinePlayerByUniqueId(playerUniqueId);
		return player == null ? translateDefault() : translate(player);
	}

	@Nonnull
	default TranslatedValue translate(@Nonnull CloudOfflinePlayer player) {
		return translate(player.getLanguage());
	}

	@Nonnull
	TranslatedValue translate(@Nonnull Language language);

}
