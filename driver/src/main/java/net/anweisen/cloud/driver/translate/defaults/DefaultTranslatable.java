package net.anweisen.cloud.driver.translate.defaults;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
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
		return Preconditions.checkNotNull(CloudDriver.getInstance().getTranslationManager().getLanguage(language), "Unknown language '" + language + "'").getValue(name);
	}

	@Nonnull
	@Override
	public TranslatedValue translate(@Nonnull UUID playerUniqueId) {
		return translate(CloudDriver.getInstance().getPlayerManager().getOfflinePlayerByUniqueId(playerUniqueId));
	}

	@Nonnull
	@Override
	public TranslatedValue translate(@Nonnull CloudOfflinePlayer player) {
		return translate(player.getLanguage());
	}

	@Override
	public String toString() {
		return "Translatable[name=" + name + "]";
	}
}
