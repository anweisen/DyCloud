package net.anweisen.cloud.driver.translate.defaults;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.translate.Language;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.cloud.driver.translate.TranslatedValue;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultTranslatable implements Translatable {

	private final String name;

	public DefaultTranslatable(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nonnull
	@Override
	public TranslatedValue translateDefault() {
		return translate(CloudDriver.getInstance().getTranslationManager().getDefaultLanguage());
	}

	@Nonnull
	@Override
	public TranslatedValue translate(@Nonnull String language) {
		return translate(Preconditions.checkNotNull(CloudDriver.getInstance().getTranslationManager().getLanguage(language), "Unknown language '" + language + "'"));
	}

	@Nonnull
	@Override
	public TranslatedValue translate(@Nonnull UUID playerUniqueId) {
		CloudOfflinePlayer player = CloudDriver.getInstance().getPlayerManager().getOfflinePlayerByUniqueId(playerUniqueId);
		return player == null ? translateDefault() : translate(player);
	}

	@Nonnull
	@Override
	public TranslatedValue translate(@Nonnull CloudOfflinePlayer player) {
		return translate(player.getLanguage());
	}

	@Nonnull
	@Override
	public TranslatedValue translate(@Nonnull Language language) {
		return language.getValue(name);
	}

	@Override
	public String toString() {
		return "Translatable[name=" + name + "]";
	}
}
