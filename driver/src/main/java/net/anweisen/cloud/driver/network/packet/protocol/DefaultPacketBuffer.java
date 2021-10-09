package net.anweisen.cloud.driver.network.packet.protocol;

import net.anweisen.cloud.driver.network.packet.protocol.objects.SerializableDocument;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.SimpleCollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
		return readOptional(this::readArray);
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalArray(@Nullable byte[] array) {
		return writeOptional(array, this::writeArray);
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
		return readOptional(this::readString);
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalString(@Nullable String string) {
		return writeOptional(string, this::writeString);
	}

	@Nonnull
	@Override
	public List<String> readStringCollection() {
		return readCollection(this::readString);
	}

	@Nonnull
	@Override
	public PacketBuffer writeStringCollection(@Nonnull Collection<? extends String> strings) {
		return writeCollection(strings, this::writeString);
	}

	@Nonnull
	@Override
	public String[] readStringArray() {
		return readArray(String.class, this::readString);
	}

	@Nonnull
	@Override
	public PacketBuffer writeStringArray(@Nonnull String[] strings) {
		return writeArray(strings, this::writeString);
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
		return readOptional(this::readUniqueId);
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalUniqueId(@Nullable UUID uniqueId) {
		return writeOptional(uniqueId, this::writeUniqueId);
	}

	@Nonnull
	@Override
	public List<UUID> readUniqueIdCollection() {
		return readCollection(this::readUniqueId);
	}

	@Nonnull
	@Override
	public PacketBuffer writeUniqueIdCollection(@Nonnull Collection<? extends UUID> uniqueIds) {
		return writeCollection(uniqueIds, this::writeUniqueId);
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
	public List<Document> readDocumentCollection() {
		return (List<Document>) (List<? extends Document>) readObjectCollection(SerializableDocument.class);
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
			Constructor<T> constructor = objectClass.getDeclaredConstructor();
			constructor.setAccessible(true);

			T object = constructor.newInstance();
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
		return readOptional(() -> readObject(objectClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalObject(@Nullable SerializableObject object) {
		return writeOptional(object, this::writeObject);
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> Collection<T> readObjectCollection(@Nonnull Class<T> objectClass) {
		return readCollection(() -> readObject(objectClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeObjectCollection(@Nonnull Collection<? extends SerializableObject> objects) {
		return writeCollection(objects, this::writeObject);
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> T[] readObjectArray(@Nonnull Class<T> objectClass) {
		return readArray(objectClass, () -> readObject(objectClass));
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> PacketBuffer writeObjectArray(@Nonnull T[] objects) {
		return writeArray(objects, this::writeObject);
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
		return readOptional(() -> readEnum(enumClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeOptionalEnum(@Nonnull Enum<?> value) {
		return writeOptional(value, this::writeEnum);
	}

	@Nonnull
	@Override
	public <E extends Enum<?>> List<E> readEnumCollection(@Nonnull Class<E> enumClass) {
		return readCollection(() -> readEnum(enumClass));
	}

	@Nonnull
	@Override
	public PacketBuffer writeEnumCollection(@Nonnull Collection<? extends Enum<?>> enums) {
		return writeCollection(enums, this::writeEnum);
	}

	@Nonnull
	public <T> List<T> readCollection(@Nonnull Supplier<T> reader) {
		int length = readVarInt();
		List<T> collection = new ArrayList<>(length);

		for (int i = 0; i < length; i++) {
			collection.add(reader.get());
		}

		return collection;
	}
	@Nonnull
	public <T> PacketBuffer writeCollection(@Nonnull Collection<? extends T> collection, @Nonnull Consumer<T> writer) {
		writeVarInt(collection.size());
		for (T object : collection) {
			writer.accept(object);
		}
		return this;
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> T[] readArray(@Nonnull Class<T> theClass, @Nonnull Supplier<T> reader) {
		int length = readVarInt();
		Object array = Array.newInstance(theClass, length);

		for (int i = 0; i < length; i++) {
			Array.set(array, i, reader.get());
		}

		return (T[]) array;
	}

	@Nonnull
	public <T> PacketBuffer writeArray(@Nonnull T[] array, @Nonnull Consumer<T> writer) {
		writeVarInt(array.length);
		for (T object : array) {
			writer.accept(object);
		}
		return this;
	}

	@Nullable
	public <T> T readOptional(@Nonnull Supplier<T> reader) {
		return readBoolean() ? reader.get() : null;
	}

	@Nonnull
	public <T> PacketBuffer writeOptional(@Nullable T object, @Nonnull Consumer<T> writer) {
		writeBoolean(object != null);
		if (object != null)
			writer.accept(object);
		return this;
	}
}
