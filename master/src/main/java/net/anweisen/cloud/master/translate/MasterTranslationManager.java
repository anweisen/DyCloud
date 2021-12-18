package net.anweisen.cloud.master.translate;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.translate.*;
import net.anweisen.cloud.driver.translate.defaults.DefaultCachedLanguage;
import net.anweisen.cloud.driver.translate.defaults.DefaultLanguageSection;
import net.anweisen.cloud.driver.translate.defaults.DefaultTranslatable;
import net.anweisen.cloud.driver.translate.defaults.DefaultTranslatedValue;
import net.anweisen.utility.common.misc.FileUtils;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterTranslationManager implements TranslationManager, LoggingApiUser {

	private static final Path directory = Paths.get("translations");
	private static final String configPath = "_.json";

	private final Map<String, Language> languages = new LinkedHashMap<>();

	private String defaultLanguage = "en"; // TODO customizable

	@Nonnull
	@Override
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

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
	public boolean hasLanguage(@Nonnull String id) {
		return languages.containsKey(id);
	}

	@Override
	public void setDefaultLanguage(@Nonnull String language) {
		this.defaultLanguage = language;
	}

	@Override
	public void setLanguageCache(@Nonnull Collection<? extends Language> languages) {
		this.languages.clear();
		for (Language language : languages)
			this.languages.put(language.getId(), language);
	}

	public void setup() {
		FileUtils.createDirectory(directory);

		List<Document> documents = Documents.newJsonBundle(getClass().getClassLoader().getResourceAsStream("language/_.json")).toDocuments();
		for (Document document : documents) {
			try {
				InputStream stream = getClass().getClassLoader().getResourceAsStream("language/" + document.getString("file"));
				if (stream == null) {
					warn("Language file for {} could not be found", document);
					continue;
				}
				Document values = Documents.newJsonDocument(stream);

				Path languageDirectory = directory.resolve(FileUtils.getFileName(document.getString("file")));
				FileUtils.createDirectory(languageDirectory);

				// Write language file
				Path languageConfig = languageDirectory.resolve(configPath);
				if (!Files.exists(languageConfig)) {
					Documents.newJsonDocument(
						"name", document.getString("name"),
						"name.local", document.getString("name.local")
					).saveToFile(languageConfig);
				}

				// Write new translations to cloud section
				Path cloudSection = languageDirectory.resolve("cloud.json");
				Document existing = Documents.newJsonDocument(cloudSection);
				values.forEach((key, value) -> {
					if (!existing.contains(key))
						existing.set(key, value);
				});
				values.saveToFile(cloudSection);

			} catch (Exception ex) {
				error("Could not copy default language {}", document, ex);
			}
		}
	}

	@Override
	public void retrieve() {
		languages.clear();

		for (Path languageDirectory : FileUtils.list(directory).filter(Files::isDirectory).collect(Collectors.toList())) {
			String languageId = languageDirectory.getFileName().toString();

			Path config = languageDirectory.resolve(configPath);
			Document data = Documents.newJsonDocumentUnchecked(config);

			DefaultCachedLanguage language = new DefaultCachedLanguage(languageId, new LanguageConfig(data));
			languages.put(languageId, language);

			for (Path sectionPath : FileUtils.list(languageDirectory).filter(current -> !current.getFileName().toString().equals(configPath)).collect(Collectors.toList())) {

				Map<String, TranslatedValue> values = new LinkedHashMap<>();
				DefaultLanguageSection section = new DefaultLanguageSection(language, FileUtils.getFileName(sectionPath.getFileName()), values);
				Document sectionData = Documents.newJsonDocumentUnchecked(sectionPath);
				for (String key : sectionData.keys()) {
					values.put(key, new DefaultTranslatedValue(section, key, sectionData.getStrings(key)));
				}

				language.registerSection(section);
				trace("=> {} - {} ({})", language.getId(), section.getId(), sectionPath);

			}

		}
	}
}
