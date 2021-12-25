package net.anweisen.cloud.driver.translate.defaults;

import net.anweisen.cloud.driver.translate.Language;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.cloud.driver.translate.TranslatedValue;

import javax.annotation.Nonnull;

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
	public TranslatedValue translate(@Nonnull Language language) {
		return language.getValue(name);
	}

	@Override
	public String toString() {
		return "Translatable[name=" + name + "]";
	}
}
