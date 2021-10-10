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
	 * @return the parent language
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

	boolean hasValue(@Nonnull String name);

	/**
	 * Gets the corresponding value if it {@link #hasValue(String) exists} or creates a new empty value using {@link #createEmptyValue(String)}
	 *
	 * @return the corresponding value or an empty value
	 */
	@Nonnull
	TranslatedValue getValue(@Nonnull String name);

	/**
	 * Returns a new empty {@link TranslatedValue} for the given name
	 *
	 * @return a new empty {@link TranslatedValue}
	 */
	@Nonnull
	TranslatedValue createEmptyValue(@Nonnull String name);

}
