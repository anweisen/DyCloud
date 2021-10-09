package net.anweisen.cloud.driver.translate.defaults;

import net.anweisen.cloud.driver.translate.LanguageConfig;
import net.anweisen.cloud.driver.translate.LanguageSection;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCachedLanguage extends AbstractLanguage {

	public DefaultCachedLanguage(@Nonnull String id, @Nonnull LanguageConfig config) {
		super(id, config);
	}

	@Nonnull
	@Override
	public LanguageSection getSection(@Nonnull String id) {
		LanguageSection section = sections.get(id);
		if (section != null) return section;

		sections.put(id, section = new DefaultLanguageSection(this, id, Collections.emptyMap()));
		return section;
	}

	public void registerSection(@Nonnull LanguageSection section) {
		sections.put(section.getId(), section);
	}
}
