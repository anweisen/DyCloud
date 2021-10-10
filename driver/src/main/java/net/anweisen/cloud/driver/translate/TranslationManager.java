package net.anweisen.cloud.driver.translate;

import net.anweisen.cloud.driver.CloudDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getTranslationManager()
 */
public interface TranslationManager {

	@Nonnull
	String getDefaultLanguage();

	/**
	 * @return a new {@link Translatable} with the given name
	 *
	 * @see Translatable#of(String)
	 */
	@Nonnull
	Translatable getTranslatable(@Nonnull String name);

	@Nonnull
	Collection<Language> getAvailableLanguages();

	@Nullable
	Language getLanguage(@Nonnull String id);

	boolean hasLanguage(@Nonnull String id);

	void setDefaultLanguage(@Nonnull String language);

	void setLanguages(@Nonnull Collection<? extends Language> languages);

	void retrieve();

}
