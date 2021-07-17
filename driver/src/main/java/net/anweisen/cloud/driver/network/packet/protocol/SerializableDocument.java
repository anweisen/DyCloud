package net.anweisen.cloud.driver.network.packet.protocol;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.SerializableObject;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.document.wrapper.WrappedDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SerializableDocument implements WrappedDocument, SerializableObject {

	@Nonnull
	public static SerializableObject asSerializable(@Nonnull Document document) {
		return new SerializableDocument(document);
	}

	private Document document;

	public SerializableDocument(@Nullable Document document) {
		this.document = document;
	}

	public SerializableDocument() {
	}

	@Override
	public Document getWrappedDocument() {
		Preconditions.checkNotNull(document, "Not read yet");
		return document;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(document.toJson());
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		document = Document.parseJson(buffer.readString());
	}
}
