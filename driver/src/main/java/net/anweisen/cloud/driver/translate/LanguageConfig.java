package net.anweisen.cloud.driver.translate;

import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class LanguageConfig implements SerializableObject {

	private Document data;

	private LanguageConfig() {
	}

	public LanguageConfig(@Nonnull Document data) {
		this.data = data;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeDocument(data);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		data = buffer.readDocument();
	}

	@Nonnull
	public String getName() {
		return data.getString("name");
	}

	@Nonnull
	public String getLocalName() {
		return data.getString("name.local");
	}

	@Nonnull
	public Document getRawData() {
		return data;
	}

	@Override
	public String toString() {
		return "LanguageConfig[name=" + getName() + " local=" + getLocalName() + "]";
	}
}
