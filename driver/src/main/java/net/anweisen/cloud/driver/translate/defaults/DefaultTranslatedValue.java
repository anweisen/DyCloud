package net.anweisen.cloud.driver.translate.defaults;

import net.anweisen.cloud.driver.translate.LanguageSection;
import net.anweisen.cloud.driver.translate.TranslatedValue;
import net.anweisen.utilities.common.misc.StringUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultTranslatedValue implements TranslatedValue {

	private final LanguageSection section;
	private final String name;

	private final List<String> value;

	public DefaultTranslatedValue(@Nonnull LanguageSection section, @Nonnull String name, @Nonnull List<String> value) {
		this.section = section;
		this.name = name;
		this.value = value;
	}

	@Nonnull
	@Override
	public LanguageSection getSection() {
		return section;
	}

	@Nonnull
	@Override
	public String getFullName() {
		return section.getId() + "." + name;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nonnull
	public List<String> getValue() {
		return value;
	}

	@Nonnull
	@Override
	public String asString(@Nonnull Object... args) {
		String string = value.size() == 1 ? value.get(0) : StringUtils.getIterableAsString(value, "\n", Function.identity());
		return StringUtils.format(string, args);
	}

	@Nonnull
	@Override
	public String[] asArray(@Nonnull Object... args) {
		String[] array = value.toArray(new String[0]);
		return StringUtils.format(array, args);
	}

	@Nonnull
	@Override
	public List<String> asList(@Nonnull Object... args) {
		return Arrays.asList(asArray(args));
	}
}
