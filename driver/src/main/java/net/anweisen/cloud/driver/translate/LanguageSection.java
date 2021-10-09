package net.anweisen.cloud.driver.translate;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see Language#getSection(String)
 * @see Language#getLoadedSections()
 */
public interface LanguageSection {

	/**
	 * @return the parent section
	 */
	@Nonnull
	Language getParent();

	/**
	 * Returns the id of this section.
	 * The id can be used in {@link Language#getSection(String)}.
	 *
	 * @return the id of this section
	 */
	@Nonnull
	String getId();

	@Nonnull
	Map<String, TranslatedValue> getValues();

	@Nonnull
	TranslatedValue getValue(@Nonnull String name);

}
