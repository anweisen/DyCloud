package net.anweisen.cloud.driver.network.packet.protocol;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see Packet#getBuffer()
 * @see SerializableObject
 */
public interface PacketBuffer {

	/**
	 * @return the amount of total bytes
	 */
	int length();

	/**
	 * @return the amount of remaining readable bytes
	 */
	int remaining();

	boolean remain(int amount);

	@Nonnull
	byte[] asArray();

	void read(@Nonnull byte[] bytes);

	void read(@Nonnull OutputStream out, int length) throws IOException;

	@Nonnull
	PacketBuffer write(@Nonnull byte[] bytes);

	@Nonnull
	PacketBuffer write(@Nonnull byte[] bytes, int index, int length);

	boolean readBoolean();

	@Nonnull
	PacketBuffer writeByte(byte value);

	byte readByte();

	@Nonnull
	PacketBuffer writeBoolean(boolean value);

	int readInt();

	@Nonnull
	PacketBuffer writeInt(int value);

	int readVarInt();

	@Nonnull
	PacketBuffer writeVarInt(int value);

	long readLong();

	@Nonnull
	PacketBuffer writeLong(long value);

	long readVarLong();

	@Nonnull
	PacketBuffer writeVarLong(long value);

	float readFloat();

	@Nonnull
	PacketBuffer writeFloat(float value);

	double readDouble();

	@Nonnull
	PacketBuffer writeDouble(double value);

	char readChar();

	@Nonnull
	PacketBuffer writeChar(char value);

	@Nonnull
	byte[] readArray();

	@Nonnull
	PacketBuffer writeArray(@Nonnull byte[] array);

	@Nullable
	byte[] readOptionalArray();

	@Nonnull
	PacketBuffer writeOptionalArray(@Nullable byte[] array);

	@Nonnull
	String readString();

	@Nonnull
	PacketBuffer writeString(@Nonnull String string);

	@Nullable
	String readOptionalString();

	@Nonnull
	PacketBuffer writeOptionalString(@Nullable String string);

	@Nonnull
	List<String> readStringCollection();

	@Nonnull
	PacketBuffer writeStringCollection(@Nonnull Collection<? extends String> strings);

	@Nonnull
	String[] readStringArray();

	@Nonnull
	PacketBuffer writeStringArray(@Nonnull String[] strings);

	@Nonnull
	UUID readUniqueId();

	@Nonnull
	PacketBuffer writeUniqueId(@Nonnull UUID uniqueId);

	@Nullable
	UUID readOptionalUniqueId();

	@Nonnull
	PacketBuffer writeOptionalUniqueId(@Nullable UUID uniqueId);

	@Nonnull
	List<UUID> readUniqueIdCollection();

	@Nonnull
	PacketBuffer writeUniqueIdCollection(@Nonnull Collection<? extends UUID> uniqueIds);

	@Nonnull
	PacketBuffer writeInetAddress(@Nonnull InetAddress address);

	@Nonnull
	InetAddress readInetAddress();

	@Nonnull
	Document readDocument();

	@Nonnull
	PacketBuffer writeDocument(@Nonnull Document document);

	@Nullable
	Document readOptionalDocument();

	@Nonnull
	PacketBuffer writeOptionalDocument(@Nullable Document document);

	@Nonnull
	List<Document> readDocumentCollection();

	@Nonnull
	PacketBuffer writeDocumentCollection(@Nonnull Collection<? extends Document> documents);

	@Nonnull
	Document[] readDocumentArray();

	@Nonnull
	PacketBuffer writeDocumentArray(@Nonnull Document[] documents);

	@Nonnull
	<T extends SerializableObject> T readObject(@Nonnull Class<T> objectClass);

	@Nonnull
	PacketBuffer writeObject(@Nonnull SerializableObject object);

	@Nullable
	<T extends SerializableObject> T readOptionalObject(@Nonnull Class<T> objectClass);

	@Nonnull
	PacketBuffer writeOptionalObject(@Nullable SerializableObject object);

	@Nonnull
	<T extends SerializableObject> Collection<T> readObjectCollection(@Nonnull Class<T> objectClass);

	@Nonnull
	PacketBuffer writeObjectCollection(@Nonnull Collection<? extends SerializableObject> objects);

	@Nonnull
	<T extends SerializableObject> T[] readObjectArray(@Nonnull Class<T> objectClass);

	@Nonnull
	<T extends SerializableObject> PacketBuffer writeObjectArray(@Nonnull T[] objects);

	@Nonnull
	<E extends Enum<?>> E readEnum(@Nonnull Class<E> enumClass);

	@Nonnull
	PacketBuffer writeEnum(@Nonnull Enum<?> value);

	@Nullable
	<E extends Enum<?>> E readOptionalEnum(@Nonnull Class<E> enumClass);

	@Nonnull
	PacketBuffer writeOptionalEnum(@Nonnull Enum<?> value);

	@Nonnull
	<E extends Enum<?>> List<E> readEnumCollection(@Nonnull Class<E> enumClass);

	@Nonnull
	PacketBuffer writeEnumCollection(@Nonnull Collection<? extends Enum<?>> enums);

	@Nonnull
	Throwable readThrowable();

	@Nonnull
	PacketBuffer writeThrowable(@Nonnull Throwable value);

	@Nonnull
	PacketBuffer release();

	@Nonnull
	PacketBuffer copy();

	@Nonnull
	<T> List<T> readCollection(@Nonnull Supplier<T> reader);

	@Nonnull
	<T> PacketBuffer writeCollection(@Nonnull Collection<? extends T> collection, @Nonnull Consumer<T> writer);

	@Nonnull
	<T> T[] readArray(@Nonnull Class<T> theClass, @Nonnull Supplier<T> reader);

	@Nonnull
	<T> PacketBuffer writeArray(@Nonnull T[] array, @Nonnull Consumer<T> writer);

	@Nullable
	<T> T readOptional(@Nonnull Supplier<T> reader);

	@Nonnull
	<T> PacketBuffer writeOptional(@Nullable T object, @Nonnull Consumer<T> writer);

}
