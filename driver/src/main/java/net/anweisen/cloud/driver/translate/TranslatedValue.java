package net.anweisen.cloud.driver.translate;

import net.anweisen.cloud.driver.player.chat.ChatText;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * {n}       -> args[n]         | argument at given index
 * ${t}      -> trans(t)        | given translation; in section or other section
 * (t)(e:c)  -> text(t, e, c)   | embed events in ChatText; t=text e=eventType c=eventContent
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see LanguageSection#getValue(String)
 * @see LanguageSection#getValues()
 *
 * @see Translatable#translate(String)
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

	@Nonnull
	ChatText[] asText(@Nonnull Object... args);

}
