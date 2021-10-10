package net.anweisen.cloud.driver.translate.defaults;

import net.anweisen.cloud.driver.translate.Language;
import net.anweisen.cloud.driver.translate.LanguageSection;
import net.anweisen.cloud.driver.translate.TranslatedValue;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultLanguageSection implements LanguageSection {

	private final Language parent;
	private final String id;
	private final Map<String, TranslatedValue> values;

	public DefaultLanguageSection(@Nonnull Language parent, @Nonnull String id, @Nonnull Map<String, TranslatedValue> values) {
		this.parent = parent;
		this.id = id;
		this.values = values;
	}

	@Nonnull
	@Override
	public Language getParent() {
		return parent;
	}

	@Nonnull
	@Override
	public String getId() {
		return id;
	}

	@Nonnull
	@Override
	public Map<String, TranslatedValue> getValues() {
		return Collections.unmodifiableMap(values);
	}

	@Override
	public boolean hasValue(@Nonnull String name) {
		return values.containsKey(name);
	}

	@Nonnull
	@Override
	public TranslatedValue getValue(@Nonnull String name) {
		TranslatedValue value = values.get(name);
		if (value != null) return value;

		values.put(name, value = createEmptyValue(name));
		return value;
	}

	@Nonnull
	@Override
	public TranslatedValue createEmptyValue(@Nonnull String name) {
		return new DefaultTranslatedValue(this, name, Collections.singletonList("{" + name + "}"));
	}
}
