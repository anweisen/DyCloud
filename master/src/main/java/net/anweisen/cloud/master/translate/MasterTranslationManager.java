package net.anweisen.cloud.master.translate;

import net.anweisen.cloud.driver.translate.Language;
import net.anweisen.cloud.driver.translate.LanguageConfig;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.cloud.driver.translate.TranslationManager;
import net.anweisen.cloud.driver.translate.defaults.DefaultCachedLanguage;
import net.anweisen.cloud.driver.translate.defaults.DefaultLanguageSection;
import net.anweisen.cloud.driver.translate.defaults.DefaultTranslatable;
import net.anweisen.cloud.driver.translate.defaults.DefaultTranslatedValue;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterTranslationManager implements TranslationManager {

	private static final Path directory = Paths.get("translations");
	private static final String configPath = ".json";

	private final Map<String, Language> languages = new LinkedHashMap<>();

	@Nonnull
	@Override
	public Translatable getTranslatable(@Nonnull String name) {
		return new DefaultTranslatable(name);
	}

	@Nonnull
	@Override
	public Collection<Language> getAvailableLanguages() {
		return Collections.unmodifiableCollection(languages.values());
	}

	@Nullable
	@Override
	public Language getLanguage(@Nonnull String id) {
		return languages.get(id);
	}

	@Override
	public void setLanguages(@Nonnull Collection<? extends Language> languages) {
		this.languages.clear();
		for (Language language : languages)
			this.languages.put(language.getId(), language);
	}

	@Override
	public void retrieve() {
		languages.clear();

		for (Path languageDirectory : FileUtils.list(directory).collect(Collectors.toList())) {
			String languageId = languageDirectory.getFileName().toString();

			Path config = languageDirectory.resolve(configPath);
			Document data = Document.readJsonFile(config);

			DefaultCachedLanguage language = new DefaultCachedLanguage(languageId, new LanguageConfig(data));

			for (Path sectionPath : FileUtils.list(languageDirectory).filter(current -> !current.getFileName().toString().equals(configPath)).collect(Collectors.toList())) {

				Map<String, DefaultTranslatedValue> values = new LinkedHashMap<>();
				DefaultLanguageSection section = new DefaultLanguageSection(language, FileUtils.getFileName(sectionPath), values);
				Document sectionData = Document.readJsonFile(sectionPath);
				for (String key : sectionData.keys()) {
					values.put(key, new DefaultTranslatedValue(section, key, sectionData.getStringList(key)));
				}

				language.registerSection(section);

			}

		}
	}
}
