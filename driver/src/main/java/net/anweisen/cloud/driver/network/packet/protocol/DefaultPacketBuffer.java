package net.anweisen.cloud.driver.network.packet.protocol;

import net.anweisen.cloud.driver.network.packet.protocol.objects.SerializableDocument;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.SimpleCollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultPacketBuffer implements PacketBuffer {

	@Nonnull
	@Override
	public byte[] readArray() {
		int length = readVarInt();

		byte[] array = new byte[length];
		read(array);

		return array;
	}

	@Nonnull
	@Override
	public PacketBuffer writeArray(@Nonnull byte[] array) {
		writeVarInt(array.length);
		write(array);
		return this;
	}

	@Nullable
	@Override
	public byte[] readOptionalArray() {
		return _readOpt(this::readArray);
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalArray(@Nullable byte[] array) {
		return _writeOpt(array, this::writeArray);
	}

	@Nonnull
	@Override
	public String readString() {
		return new String(readArray(), StandardCharsets.UTF_8);
	}

	@Nonnull
	@Override
	public PacketBuffer writeString(@Nonnull String string) {
		writeArray(string.getBytes(StandardCharsets.UTF_8));
		return this;
	}

	@Nullable
	@Override
	public String readOptionalString() {
		return _readOpt(this::readString);
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalString(@Nullable String string) {
		return _writeOpt(string, this::writeString);
	}

	@Nonnull
	@Override
	public Collection<String> readStringCollection() {
		return _readCollection(this::readString);
	}

	@Nonnull
	@Override
	public PacketBuffer writeStringCollection(@Nonnull Collection<? extends String> strings) {
		return _writeCollection(strings, this::writeString);
	}

	@Nonnull
	@Override
	public String[] readStringArray() {
		return _readArray(String.class, this::readString);
	}

	@Nonnull
	@Override
	public PacketBuffer writeStringArray(@Nonnull String[] strings) {
		return _writeArray(strings, this::writeString);
	}

	@Nonnull
	@Override
	public UUID readUniqueId() {
		return new UUID(readLong(), readLong());
	}

	@Nonnull
	@Override
	public PacketBuffer writeUniqueId(@Nonnull UUID uniqueId) {
		writeLong(uniqueId.getMostSignificantBits());
		writeLong(uniqueId.getLeastSignificantBits());
		return this;
	}

	@Nullable
	@Override
	public UUID readOptionalUniqueId() {
		return _readOpt(this::readUniqueId);
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalUniqueId(@Nullable UUID uniqueId) {
		return _writeOpt(uniqueId, this::writeUniqueId);
	}

	@Nonnull
	@Override
	public Collection<UUID> readUniqueIdCollection() {
		return _readCollection(this::readUniqueId);
	}

	@Nonnull
	@Override
	public PacketBuffer writeUniqueIdCollection(@Nonnull Collection<? extends UUID> uniqueIds) {
		return _writeCollection(uniqueIds, this::writeUniqueId);
	}

	@Nonnull
	@Override
	public PacketBuffer writeInetAddress(@Nonnull InetAddress address) {
		writeOptionalString(address.getHostName());
		writeArray(address.getAddress());
		return this;
	}

	@Nonnull
	@Override
	public InetAddress readInetAddress() {
		try {
			return InetAddress.getByAddress(readOptionalString(), readArray());
		} catch (UnknownHostException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@Override
	public Document readDocument() {
		return readObject(SerializableDocument.class);
	}

	@Nonnull
	@Override
	public PacketBuffer writeDocument(@Nonnull Document document) {
		return writeObject(new SerializableDocument(document));
	}

	@Nullable
	@Override
	public Document readOptionalDocument() {
		return readOptionalObject(SerializableDocument.class);
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalDocument(@Nullable Document document) {
		return writeOptionalObject(new SerializableDocument(document));
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Document> readDocumentCollection() {
		return (Collection<Document>) (Collection<? extends Document>) readObjectCollection(SerializableDocument.class);
	}

	@Nonnull
	@Override
	public PacketBuffer writeDocumentCollection(@Nonnull Collection<? extends Document> documents) {
		return writeObjectCollection(SimpleCollectionUtils.convert(documents, SerializableDocument::new));
	}

	@Nonnull
	@Override
	public Document[] readDocumentArray() {
		return readObjectArray(SerializableDocument.class);
	}

	@Nonnull
	@Override
	public PacketBuffer writeDocumentArray(@Nonnull Document[] documents) {
		return writeObjectArray(SimpleCollectionUtils.convert(Arrays.asList(documents), SerializableDocument::new).toArray(new SerializableObject[0]));
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> T readObject(@Nonnull Class<T> objectClass) {
		try {
			T object = objectClass.getDeclaredConstructor().newInstance();
			object.read(this);
			return object;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@Override
	public PacketBuffer writeObject(@Nonnull SerializableObject object) {
		object.write(this);
		return this;
	}

	@Nullable
	@Override
	public <T extends SerializableObject> T readOptionalObject(@Nonnull Class<T> objectClass) {
		return _readOpt(() -> readObject(objectClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalObject(@Nullable SerializableObject object) {
		return _writeOpt(object, this::writeObject);
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> Collection<T> readObjectCollection(@Nonnull Class<T> objectClass) {
		return _readCollection(() -> readObject(objectClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeObjectCollection(@Nonnull Collection<? extends SerializableObject> objects) {
		return _writeCollection(objects, this::writeObject);
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> T[] readObjectArray(@Nonnull Class<T> objectClass) {
		return _readArray(objectClass, () -> readObject(objectClass));
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> PacketBuffer writeObjectArray(@Nonnull T[] objects) {
		return _writeArray(objects, this::writeObject);
	}

	@Nonnull
	@Override
	public <E extends Enum<?>> E readEnum(@Nonnull Class<E> enumClass) {
		return enumClass.getEnumConstants()[readVarInt()];
	}

	@Nonnull
	@Override
	public PacketBuffer writeEnum(@Nonnull Enum<?> value) {
		writeVarInt(value.ordinal());
		return this;
	}

	@Nullable
	@Override
	public <E extends Enum<?>> E readOptionalEnum(@Nonnull Class<E> enumClass) {
		return _readOpt(() -> readEnum(enumClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalEnum(@Nonnull Enum<?> value) {
		return _writeOpt(value, this::writeEnum);
	}

	@Nonnull
	@Override
	public <E extends Enum<?>> Collection<E> readEnumCollection(@Nonnull Class<E> enumClass) {
		return _readCollection(() -> readEnum(enumClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeEnumCollection(@Nonnull Collection<? extends Enum<?>> enums) {
		return _writeCollection(enums, this::writeEnum);
	}

	@Nonnull
	protected <T> Collection<T> _readCollection(@Nonnull Supplier<T> reader) {
		int length = readVarInt();
		Collection<T> collection = new ArrayList<>(length);

		for (int i = 0; i < length; i++) {
			collection.add(reader.get());
		}

		return collection;
	}
	@Nonnull
	protected <T> PacketBuffer _writeCollection(@Nonnull Collection<? extends T> collection, @Nonnull Consumer<T> writer) {
		writeVarInt(collection.size());
		for (T object : collection) {
			writer.accept(object);
		}
		return this;
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	protected <T> T[] _readArray(@Nonnull Class<T> theClass, @Nonnull Supplier<T> reader) {
		int length = readVarInt();
		Object array = Array.newInstance(theClass, length);

		for (int i = 0; i < length; i++) {
			Array.set(array, i, reader.get());
		}

		return (T[]) array;
	}

	@Nonnull
	protected <T> PacketBuffer _writeArray(@Nonnull T[] array, @Nonnull Consumer<T> writer) {
		writeVarInt(array.length);
		for (T object : array) {
			writer.accept(object);
		}
		return this;
	}

	@Nullable
	protected <T> T _readOpt(@Nonnull Supplier<T> reader) {
		return readBoolean() ? reader.get() : null;
	}

	@Nonnull
	protected <T> PacketBuffer _writeOpt(@Nullable T object, @Nonnull Consumer<T> writer) {
		writeBoolean(object != null);
		if (object != null)
			writer.accept(object);
		return this;
	}
}
