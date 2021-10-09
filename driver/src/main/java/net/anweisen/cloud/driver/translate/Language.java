package net.anweisen.cloud.driver.translate;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see TranslationManager#getLanguage(String)
 * @see TranslationManager#getAvailableLanguages()
 */
public interface Language {

	@Nonnull
	String getId();

	@Nonnull
	LanguageConfig getConfig();

	@Nonnull
	Collection<LanguageSection> getLoadedSections();

	@Nonnull
	LanguageSection getSection(@Nonnull String id);

	@Nonnull
	TranslatedValue getValue(@Nonnull String name);

}
