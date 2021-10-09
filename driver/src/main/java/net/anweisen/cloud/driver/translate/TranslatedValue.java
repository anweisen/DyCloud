package net.anweisen.cloud.driver.translate;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface TranslatedValue {

	/**
	 * @return the parent section
	 */
	@Nonnull
	LanguageSection getSection();

	/**
	 * Returns the full name which consists of the section name and the translation name.
	 * The full name can be used in {@link Language#getValue(String)}.
	 *
	 * @return the full name which is the section name and the translation name
	 */
	@Nonnull
	String getFullName();

	/**
	 * Returns the section specific name.
	 * The section specific name can be used in {@link LanguageSection#getValue(String)}.
	 *
	 * @return the section specific name
	 */
	@Nonnull
	String getName();

	@Nonnull
	String asString(@Nonnull Object... args);

	@Nonnull
	String[] asArray(@Nonnull Object... args);

	@Nonnull
	List<String> asList(@Nonnull Object... args);

}
