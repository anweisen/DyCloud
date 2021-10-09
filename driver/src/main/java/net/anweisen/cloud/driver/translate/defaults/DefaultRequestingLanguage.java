package net.anweisen.cloud.driver.translate.defaults;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.NetworkingApiUser;
import net.anweisen.cloud.driver.network.packet.def.TranslationSystemPacket;
import net.anweisen.cloud.driver.network.packet.def.TranslationSystemPacket.TranslationPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.translate.LanguageConfig;
import net.anweisen.cloud.driver.translate.LanguageSection;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultRequestingLanguage extends AbstractLanguage implements LoggingApiUser, NetworkingApiUser {

	public DefaultRequestingLanguage(@Nonnull String id, @Nonnull LanguageConfig config) {
		super(id, config);
	}

	@Nonnull
	@Override
	public LanguageSection getSection(@Nonnull String id) {
		LanguageSection section = sections.get(id);
		if (section != null) return section;

		// Request section from mater
		synchronized (this) {

			debug("Requesting language section '{}' for language '{}'", id, this.id);
			PacketBuffer response = sendPacketQuery(new TranslationSystemPacket(TranslationPayload.GET_SECTION, buffer -> buffer.writeString(this.id).writeString(id))).getBuffer();

			Map<String, DefaultTranslatedValue> values = new LinkedHashMap<>();
			section = new DefaultLanguageSection(this, id, values);

			while (response.remain(1)) {
				String name = response.readString();
				List<String> value = response.readStringCollection();
				values.put(name, new DefaultTranslatedValue(section, name, value));
			}

			sections.put(id, section);

			return section;
		}
	}
}
