package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.TranslationSystemPacket.TranslationPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.translate.Language;
import net.anweisen.cloud.driver.translate.LanguageSection;
import net.anweisen.cloud.driver.translate.TranslatedValue;
import net.anweisen.cloud.driver.translate.defaults.DefaultTranslatedValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class TranslationSystemListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		TranslationPayload payload = packet.getBuffer().readEnum(TranslationPayload.class);

		switch (payload) {
			case GET_SECTION: {
				String languageId = packet.getBuffer().readString();
				String sectionId = packet.getBuffer().readString();
				trace("{} -> {}.{}", payload, languageId, sectionId);

				Language language = CloudDriver.getInstance().getTranslationManager().getLanguage(languageId);
				LanguageSection section = language.getSection(sectionId);
				Map<String, TranslatedValue> values = section.getValues();

				PacketBuffer buffer = Packet.newBuffer();
				values.forEach((key, value) -> {
					List<String> list = ((DefaultTranslatedValue) value).getValue();
					buffer.writeString(key).writeStringCollection(list);
				});

				channel.sendPacket(Packet.createResponseFor(packet, buffer));
				break;
			}
			case RETRIEVE: {
				trace("{}", payload);

				PacketBuffer buffer = Packet.newBuffer();
				buffer.writeString(CloudDriver.getInstance().getTranslationManager().getDefaultLanguage());

				for (Language language : CloudDriver.getInstance().getTranslationManager().getAvailableLanguages()) {
					buffer.writeString(language.getId());
					buffer.writeObject(language.getConfig());
				}

				channel.sendPacket(Packet.createResponseFor(packet, buffer));
				break;
			}
		}

	}
}
