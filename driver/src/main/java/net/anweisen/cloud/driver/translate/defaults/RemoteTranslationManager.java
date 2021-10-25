package net.anweisen.cloud.driver.translate.defaults;

import net.anweisen.cloud.driver.network.NetworkingApiUser;
import net.anweisen.cloud.driver.network.packet.def.TranslationSystemPacket;
import net.anweisen.cloud.driver.network.packet.def.TranslationSystemPacket.TranslationPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.translate.Language;
import net.anweisen.cloud.driver.translate.LanguageConfig;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.cloud.driver.translate.TranslationManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteTranslationManager implements TranslationManager, NetworkingApiUser {

	private final Map<String, Language> languages = new LinkedHashMap<>();

	private String defaultLanguage;

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

	@Override
	public synchronized void retrieve() {
		PacketBuffer buffer = sendPacketQuery(new TranslationSystemPacket(TranslationPayload.RETRIEVE, null)).getBuffer();

		defaultLanguage = buffer.readString();

		languages.clear();
		while (buffer.remain(1)) {
			String id = buffer.readString();
			languages.put(id, new DefaultRequestingLanguage(id, buffer.readObject(LanguageConfig.class)));
		}
	}
}
