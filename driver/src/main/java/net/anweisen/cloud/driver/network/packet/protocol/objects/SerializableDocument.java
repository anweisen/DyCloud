package net.anweisen.cloud.driver.network.packet.protocol.objects;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.document.wrapper.WrappedDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SerializableDocument implements WrappedDocument<SerializableDocument>, SerializableObject {

	private Document document;

	private SerializableDocument() {
	}

	public SerializableDocument(@Nullable Document document) {
		this.document = document;
	}

	@Override
	public Document getWrappedDocument() {
		Preconditions.checkNotNull(document, "Not read yet");
		return document;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeString(document.toJson());
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		document = Document.parseJson(buffer.readString());
	}
}