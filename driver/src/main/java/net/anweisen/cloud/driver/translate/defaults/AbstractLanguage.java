package net.anweisen.cloud.driver.translate.defaults;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.translate.Language;
import net.anweisen.cloud.driver.translate.LanguageConfig;
import net.anweisen.cloud.driver.translate.LanguageSection;
import net.anweisen.cloud.driver.translate.TranslatedValue;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class AbstractLanguage implements Language {

	protected final String id;
	protected final LanguageConfig config;
	protected final Map<String, LanguageSection> sections;

	public AbstractLanguage(@Nonnull String id, @Nonnull LanguageConfig config) {
		this.id = id;
		this.config = config;
		this.sections = new LinkedHashMap<>();
	}

	@Nonnull
	@Override
	public String getId() {
		return id;
	}

	@Nonnull
	@Override
	public LanguageConfig getConfig() {
		return config;
	}

	@Nonnull
	@Override
	public Collection<LanguageSection> getLoadedSections() {
		return Collections.unmodifiableCollection(sections.values());
	}

	@Nonnull
	@Override
	public TranslatedValue getValue(@Nonnull String name) {
		String[] split = name.split("\\.");
		Preconditions.checkArgument(split.length > 1, "Illegal translation name '" + name + "'; Must consist of 'section.name'");

		LanguageSection section = getSection(split[0]);
		String subname = name.substring(name.indexOf(".") + 1);

		return section.getValue(subname);
	}

	@Override
	public String toString() {
		return "Language[id=" + id + " name=" + config.getName() + " sections=" + sections.keySet() + "]";
	}
}
