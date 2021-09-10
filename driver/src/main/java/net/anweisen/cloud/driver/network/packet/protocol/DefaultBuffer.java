package net.anweisen.cloud.driver.network.packet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.util.ByteProcessor;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultBuffer extends Buffer {

	private final ByteBuf wrapped;

	public DefaultBuffer(@Nonnull ByteBuf wrapped) {
		this.wrapped = wrapped;
	}

	@Nonnull
	@Override
	public Buffer writeString(@Nonnull String stringToWrite) {
		writeArray(stringToWrite.getBytes(StandardCharsets.UTF_8));
		return this;
	}

	@Override
	public String readOptionalString() {
		return readBoolean() ? readString() : null;
	}

	@Override
	public Buffer writeOptionalString(@Nullable String stringToWrite) {
		writeBoolean(stringToWrite != null);
		if (stringToWrite != null)
			writeString(stringToWrite);
		return this;
	}

	@Nonnull
	@Override
	public String readString() {
		return new String(readArray(), StandardCharsets.UTF_8);
	}

	@Nonnull
	@Override
	public Buffer writeArray(@Nonnull byte[] bytes) {
		writeVarInt(bytes.length);
		writeBytes(bytes);
		return this;
	}

	@Override
	public byte[] readOptionalArray() {
		return readBoolean() ? readArray() : null;
	}

	@Nonnull
	@Override
	public Buffer writeOptionalArray(byte[] bytes) {
		writeBoolean(bytes != null);
		if (bytes != null)
			writeArray(bytes);
		return this;
	}

	@Nonnull
	@Override
	public byte[] readArray() {
		int length = readVarInt();

		byte[] bytes = new byte[length];
		readBytes(bytes);

		return bytes;
	}

	@Nonnull
	@Override
	public byte[] toArray() {
		byte[] bytes = new byte[readableBytes()];
		getBytes(readerIndex(), bytes);
		return bytes;
	}

	@Override
	public Buffer writeStringCollection(@Nonnull Collection<String> list) {
		writeVarInt(list.size());
		for (String string : list)
			writeString(string);
		return this;
	}

	@Nonnull
	@Override
	public String[] readStringArray() {
		String[] array = new String[readVarInt()];
		for (int i = 0; i < array.length; i++) {
			array[i] = readString();
		}
		return array;
	}

	@Nonnull
	@Override
	public Buffer writeStringArray(@Nonnull String[] array) {
		writeVarInt(array.length);
		for (String string : array) {
			writeString(string);
		}
		return this;
	}

	@Nonnull
	@Override
	public Collection<String> readStringCollection() {
		int length = readVarInt();
		List<String> out = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			out.add(readString());
		}

		return out;
	}

	@Override
	public int readVarInt() {
		return NettyUtils.readVarInt(this);
	}

	@Nonnull
	@Override
	public Buffer writeVarInt(int value) {
		NettyUtils.writeVarInt(this, value);
		return this;
	}

	@Override
	public long readVarLong() {
		return NettyUtils.readVarLong(this);
	}

	@Nonnull
	@Override
	public Buffer writeVarLong(long value) {
		NettyUtils.writeVarLong(this, value);
		return this;
	}

	@Nonnull
	@Override
	public UUID readUUID() {
		return new UUID(readLong(), readLong());
	}

	@Nonnull
	@Override
	public Buffer writeUUID(@Nonnull UUID uuid) {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
		return this;
	}

	@Override
	public UUID readOptionalUUID() {
		return readBoolean() ? readUUID() : null;
	}

	@Nonnull
	@Override
	public Buffer writeOptionalUUID(@Nullable UUID uuid) {
		writeBoolean(uuid != null);
		if (uuid != null)
			writeUUID(uuid);
		return this;
	}

	@Nonnull
	@Override
	public Collection<UUID> readUUIDCollection() {
		int size = readVarInt();
		Collection<UUID> uuids = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			uuids.add(readUUID());
		}
		return uuids;
	}

	@Nonnull
	@Override
	public Buffer writeUUIDCollection(@Nonnull Collection<UUID> uuids) {
		writeVarInt(uuids.size());
		for (UUID uuid : uuids)
			writeUUID(uuid);
		return this;
	}

	@Nonnull
	@Override
	public Document readDocument() {
		return readObject(SerializableDocument.class);
	}

	@Nonnull
	@Override
	public Buffer writeDocument(@Nonnull Document document) {
		return writeObject(new SerializableDocument(document));
	}

	@Override
	public Document readOptionalDocument() {
		return readBoolean() ? readDocument() : null;
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Document> readDocumentCollection() {
		Collection<? extends Document> documents = readObjectCollection(SerializableDocument.class);
		return (Collection<Document>) documents;
	}

	@Nonnull
	@Override
	public Buffer writeDocumentCollection(@Nonnull Collection<? extends Document> documents) {
		List<SerializableDocument> mapped = new ArrayList<>(documents.size());
		for (Document document : documents) {
			mapped.add(new SerializableDocument(document));
		}
		writeObjectCollection(mapped);
		return this;
	}

	@Nonnull
	@Override
	public Buffer writeOptionalDocument(@Nullable Document document) {
		writeBoolean(document != null);
		if (document != null)
			writeDocument(document);
		return this;
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> T readObject(@Nonnull Class<T> objectClass) {
		try {
			Constructor<T> constructor = objectClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			T t = constructor.newInstance();
			return readObject(t);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> T readObject(@Nonnull T targetObject) {
		targetObject.read(this);
		return targetObject;
	}

	@Nonnull
	@Override
	public Buffer writeObject(@Nonnull SerializableObject object) {
		object.write(this);
		return this;
	}

	@Override
	public <T extends SerializableObject> T readOptionalObject(@Nonnull Class<T> objectClass) {
		return readBoolean() ? readObject(objectClass) : null;
	}

	@Override
	public <T extends SerializableObject> T readOptionalObject(@Nonnull T targetObject) {
		return readBoolean() ? readObject(targetObject) : null;
	}

	@Nonnull
	@Override
	public Buffer writeOptionalObject(@Nullable SerializableObject object) {
		writeBoolean(object != null);
		if (object != null)
			writeObject(object);
		return this;
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> Collection<T> readObjectCollection(@Nonnull Class<T> objectClass) {
		int size = readVarInt();
		Collection<T> result = new ArrayList<>(size);

		try {
			Constructor<T> constructor = objectClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			for (int i = 0; i < size; i++) {
				result.add(readObject(constructor.newInstance()));
			}
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			throw new WrappedException(ex);
		}

		return result;
	}

	@Nonnull
	@Override
	public Buffer writeObjectCollection(@Nonnull Collection<? extends SerializableObject> objects) {
		writeVarInt(objects.size());
		for (SerializableObject object : objects) {
			writeObject(object);
		}
		return this;
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T extends SerializableObject> T[] readObjectArray(@Nonnull Class<T> objectClass) {
		int size = readVarInt();
		Object result = Array.newInstance(objectClass, size);

		try {
			Constructor<T> constructor = objectClass.getDeclaredConstructor();
			for (int i = 0; i < size; i++) {
				Array.set(result, i, readObject(constructor.newInstance()));
			}
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			throw new Error(ex);
		}

		return (T[]) result;
	}

	@Nonnull
	@Override
	public <T extends SerializableObject> Buffer writeObjectArray(@Nonnull T[] objects) {
		writeVarInt(objects.length);
		for (T object : objects) {
			writeObject(object);
		}
		return this;
	}

	@Override
	public <E extends Enum<E>> E readEnumConstant(@Nonnull Class<E> enumClass) {
		return enumClass.getEnumConstants()[readVarInt()];
	}

	@Override
	@Nonnull
	public <E extends Enum<E>> Buffer writeEnumConstant(@Nonnull E enumConstant) {
		writeVarInt(enumConstant.ordinal());
		return this;
	}

	@Override
	public <E extends Enum<E>> E readOptionalEnumConstant(@Nonnull Class<E> enumClass) {
		int value = readVarInt();
		return value != -1 ? enumClass.getEnumConstants()[value] : null;
	}

	@Nonnull
	@Override
	public <E extends Enum<E>> Buffer writeOptionalEnumConstant(@Nullable E enumConstant) {
		writeVarInt(enumConstant != null ? enumConstant.ordinal() : -1);
		return this;
	}

	@Nonnull
	@Override
	public Buffer writeThrowable(Throwable throwable) {
		try (ByteBufOutputStream outputStream = new ByteBufOutputStream(this);
		     ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
			objectOutputStream.writeObject(throwable);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return this;
	}

	@Override
	public Throwable readThrowable() {
		try (ByteBufInputStream inputStream = new ByteBufInputStream(this);
		     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
			return (Throwable) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public int capacity() {
		return wrapped.capacity();
	}

	@Override
	public ByteBuf capacity(int newCapacity) {
		return wrapped.capacity(newCapacity);
	}

	@Override
	public int maxCapacity() {
		return wrapped.maxCapacity();
	}

	@Override
	public ByteBufAllocator alloc() {
		return wrapped.alloc();
	}

	@Override
	@Deprecated
	public ByteOrder order() {
		return wrapped.order();
	}

	@Override
	@Deprecated
	public ByteBuf order(ByteOrder endianness) {
		return wrapped.order(endianness);
	}

	@Override
	public ByteBuf unwrap() {
		return wrapped.unwrap();
	}

	@Override
	public boolean isDirect() {
		return wrapped.isDirect();
	}

	@Override
	public boolean isReadOnly() {
		return wrapped.isReadOnly();
	}

	@Override
	public ByteBuf asReadOnly() {
		return wrapped.asReadOnly();
	}

	@Override
	public int readerIndex() {
		return wrapped.readerIndex();
	}

	@Override
	public ByteBuf readerIndex(int readerIndex) {
		return wrapped.readerIndex(readerIndex);
	}

	@Override
	public int writerIndex() {
		return wrapped.writerIndex();
	}

	@Override
	public ByteBuf writerIndex(int writerIndex) {
		return wrapped.writerIndex(writerIndex);
	}

	@Override
	public ByteBuf setIndex(int readerIndex, int writerIndex) {
		return wrapped.setIndex(readerIndex, writerIndex);
	}

	@Override
	public int readableBytes() {
		return wrapped.readableBytes();
	}

	@Override
	public int writableBytes() {
		return wrapped.writableBytes();
	}

	@Override
	public int maxWritableBytes() {
		return wrapped.maxWritableBytes();
	}

	@Override
	public boolean isReadable() {
		return wrapped.isReadable();
	}

	@Override
	public boolean isReadable(int size) {
		return wrapped.isReadable(size);
	}

	@Override
	public boolean isWritable() {
		return wrapped.isWritable();
	}

	@Override
	public boolean isWritable(int size) {
		return wrapped.isWritable(size);
	}

	@Override
	public Buffer clear() {
		wrapped.clear();
		return this;
	}

	@Override
	public Buffer markReaderIndex() {
		wrapped.markReaderIndex();
		return this;
	}

	@Override
	public Buffer resetReaderIndex() {
		wrapped.resetReaderIndex();
		return this;
	}

	@Override
	public Buffer markWriterIndex() {
		wrapped.markWriterIndex();
		return this;
	}

	@Override
	public Buffer resetWriterIndex() {
		wrapped.resetWriterIndex();
		return this;
	}

	@Override
	public Buffer discardReadBytes() {
		wrapped.discardReadBytes();
		return this;
	}

	@Override
	public Buffer discardSomeReadBytes() {
		wrapped.discardSomeReadBytes();
		return this;
	}

	@Override
	public Buffer ensureWritable(int minWritableBytes) {
		wrapped.ensureWritable(minWritableBytes);
		return this;
	}

	@Override
	public int ensureWritable(int minWritableBytes, boolean force) {
		return wrapped.ensureWritable(minWritableBytes, force);
	}

	@Override
	public boolean getBoolean(int index) {
		return wrapped.getBoolean(index);
	}

	@Override
	public byte getByte(int index) {
		return wrapped.getByte(index);
	}

	@Override
	public short getUnsignedByte(int index) {
		return wrapped.getUnsignedByte(index);
	}

	@Override
	public short getShort(int index) {
		return wrapped.getShort(index);
	}

	@Override
	public short getShortLE(int index) {
		return wrapped.getShortLE(index);
	}

	@Override
	public int getUnsignedShort(int index) {
		return wrapped.getUnsignedShort(index);
	}

	@Override
	public int getUnsignedShortLE(int index) {
		return wrapped.getUnsignedShortLE(index);
	}

	@Override
	public int getMedium(int index) {
		return wrapped.getMedium(index);
	}

	@Override
	public int getMediumLE(int index) {
		return wrapped.getMediumLE(index);
	}

	@Override
	public int getUnsignedMedium(int index) {
		return wrapped.getUnsignedMedium(index);
	}

	@Override
	public int getUnsignedMediumLE(int index) {
		return wrapped.getUnsignedMediumLE(index);
	}

	@Override
	public int getInt(int index) {
		return wrapped.getInt(index);
	}

	@Override
	public int getIntLE(int index) {
		return wrapped.getIntLE(index);
	}

	@Override
	public long getUnsignedInt(int index) {
		return wrapped.getUnsignedInt(index);
	}

	@Override
	public long getUnsignedIntLE(int index) {
		return wrapped.getUnsignedIntLE(index);
	}

	@Override
	public long getLong(int index) {
		return wrapped.getLong(index);
	}

	@Override
	public long getLongLE(int index) {
		return wrapped.getLongLE(index);
	}

	@Override
	public char getChar(int index) {
		return wrapped.getChar(index);
	}

	@Override
	public float getFloat(int index) {
		return wrapped.getFloat(index);
	}

	@Override
	public float getFloatLE(int index) {
		return wrapped.getFloatLE(index);
	}

	@Override
	public double getDouble(int index) {
		return wrapped.getDouble(index);
	}

	@Override
	public double getDoubleLE(int index) {
		return wrapped.getDoubleLE(index);
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst) {
		wrapped.getBytes(index, dst);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int length) {
		wrapped.getBytes(index, dst, length);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
		wrapped.getBytes(index, dst, dstIndex, length);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst) {
		wrapped.getBytes(index, dst);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
		wrapped.getBytes(index, dst, dstIndex, length);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuffer dst) {
		wrapped.getBytes(index, dst);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
		wrapped.getBytes(index, out, length);
		return this;
	}

	@Override
	public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
		return wrapped.getBytes(index, out, length);
	}

	@Override
	public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
		return wrapped.getBytes(index, out, position, length);
	}

	@Override
	public CharSequence getCharSequence(int index, int length, Charset charset) {
		return wrapped.getCharSequence(index, length, charset);
	}

	@Override
	public ByteBuf setBoolean(int index, boolean value) {
		wrapped.setBoolean(index, value);
		return this;
	}

	@Override
	public ByteBuf setByte(int index, int value) {
		wrapped.setByte(index, value);
		return this;
	}

	@Override
	public ByteBuf setShort(int index, int value) {
		wrapped.setShort(index, value);
		return this;
	}

	@Override
	public ByteBuf setShortLE(int index, int value) {
		wrapped.setShortLE(index, value);
		return this;
	}

	@Override
	public ByteBuf setMedium(int index, int value) {
		wrapped.setMedium(index, value);
		return this;
	}

	@Override
	public ByteBuf setMediumLE(int index, int value) {
		wrapped.setMediumLE(index, value);
		return this;
	}

	@Override
	public ByteBuf setInt(int index, int value) {
		wrapped.setInt(index, value);
		return this;
	}

	@Override
	public ByteBuf setIntLE(int index, int value) {
		wrapped.setIntLE(index, value);
		return this;
	}

	@Override
	public ByteBuf setLong(int index, long value) {
		wrapped.setLong(index, value);
		return this;
	}

	@Override
	public ByteBuf setLongLE(int index, long value) {
		wrapped.setLongLE(index, value);
		return this;
	}

	@Override
	public ByteBuf setChar(int index, int value) {
		wrapped.setChar(index, value);
		return this;
	}

	@Override
	public ByteBuf setFloat(int index, float value) {
		wrapped.setFloat(index, value);
		return this;
	}

	@Override
	public ByteBuf setFloatLE(int index, float value) {
		wrapped.setFloatLE(index, value);
		return this;
	}

	@Override
	public ByteBuf setDouble(int index, double value) {
		wrapped.setDouble(index, value);
		return this;
	}

	@Override
	public ByteBuf setDoubleLE(int index, double value) {
		wrapped.setDoubleLE(index, value);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src) {
		wrapped.setBytes(index, src);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int length) {
		wrapped.setBytes(index, src, length);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
		wrapped.setBytes(index, src, srcIndex, length);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src) {
		wrapped.setBytes(index, src);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
		wrapped.setBytes(index, src, srcIndex, length);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuffer src) {
		wrapped.setBytes(index, src);
		return this;
	}

	@Override
	public int setBytes(int index, InputStream in, int length) throws IOException {
		return wrapped.setBytes(index, in, length);
	}

	@Override
	public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
		return wrapped.setBytes(index, in, length);
	}

	@Override
	public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
		return wrapped.setBytes(index, in, position, length);
	}

	@Override
	public ByteBuf setZero(int index, int length) {
		return wrapped.setZero(index, length);
	}

	@Override
	public int setCharSequence(int index, CharSequence sequence, Charset charset) {
		return wrapped.setCharSequence(index, sequence, charset);
	}

	@Override
	public boolean readBoolean() {
		return wrapped.readBoolean();
	}

	@Override
	public byte readByte() {
		return wrapped.readByte();
	}

	@Override
	public short readUnsignedByte() {
		return wrapped.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return wrapped.readShort();
	}

	@Override
	public short readShortLE() {
		return wrapped.readShortLE();
	}

	@Override
	public int readUnsignedShort() {
		return wrapped.readUnsignedShort();
	}

	@Override
	public int readUnsignedShortLE() {
		return wrapped.readUnsignedShortLE();
	}

	@Override
	public int readMedium() {
		return wrapped.readMedium();
	}

	@Override
	public int readMediumLE() {
		return wrapped.readMediumLE();
	}

	@Override
	public int readUnsignedMedium() {
		return wrapped.readUnsignedMedium();
	}

	@Override
	public int readUnsignedMediumLE() {
		return wrapped.readUnsignedMediumLE();
	}

	@Override
	public int readInt() {
		return wrapped.readInt();
	}

	@Override
	public int readIntLE() {
		return wrapped.readIntLE();
	}

	@Override
	public long readUnsignedInt() {
		return wrapped.readUnsignedInt();
	}

	@Override
	public long readUnsignedIntLE() {
		return wrapped.readUnsignedIntLE();
	}

	@Override
	public long readLong() {
		return wrapped.readLong();
	}

	@Override
	public long readLongLE() {
		return wrapped.readLongLE();
	}

	@Override
	public char readChar() {
		return wrapped.readChar();
	}

	@Override
	public float readFloat() {
		return wrapped.readFloat();
	}

	@Override
	public float readFloatLE() {
		return wrapped.readFloatLE();
	}

	@Override
	public double readDouble() {
		return wrapped.readDouble();
	}

	@Override
	public double readDoubleLE() {
		return wrapped.readDoubleLE();
	}

	@Override
	public ByteBuf readBytes(int length) {
		return wrapped.readBytes(length);
	}

	@Override
	public ByteBuf readSlice(int length) {
		return wrapped.readSlice(length);
	}

	@Override
	public ByteBuf readRetainedSlice(int length) {
		return wrapped.readRetainedSlice(length);
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst) {
		return wrapped.readBytes(dst);
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int length) {
		return wrapped.readBytes(dst, length);
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
		return wrapped.readBytes(dst, dstIndex, length);
	}

	@Override
	public ByteBuf readBytes(byte[] dst) {
		return wrapped.readBytes(dst);
	}

	@Override
	public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
		return wrapped.readBytes(dst, dstIndex, length);
	}

	@Override
	public ByteBuf readBytes(ByteBuffer dst) {
		return wrapped.readBytes(dst);
	}

	@Override
	public ByteBuf readBytes(OutputStream out, int length) throws IOException {
		return wrapped.readBytes(out, length);
	}

	@Override
	public int readBytes(GatheringByteChannel out, int length) throws IOException {
		return wrapped.readBytes(out, length);
	}

	@Override
	public CharSequence readCharSequence(int length, Charset charset) {
		return wrapped.readCharSequence(length, charset);
	}

	@Override
	public int readBytes(FileChannel out, long position, int length) throws IOException {
		return wrapped.readBytes(out, position, length);
	}

	@Override
	public ByteBuf skipBytes(int length) {
		return wrapped.skipBytes(length);
	}

	@Nonnull
	@Override
	public Buffer writeBoolean(boolean value) {
		wrapped.writeBoolean(value);
		return this;
	}

	@Nonnull
	@Override
	public Buffer writeByte(int value) {
		wrapped.writeByte(value);
		return this;
	}

	@Nonnull
	@Override
	public Buffer writeShort(int value) {
		wrapped.writeShort(value);
		return this;
	}

	@Override
	public ByteBuf writeShortLE(int value) {
		return wrapped.writeShortLE(value);
	}

	@Override
	public ByteBuf writeMedium(int value) {
		return wrapped.writeMedium(value);
	}

	@Override
	public ByteBuf writeMediumLE(int value) {
		return wrapped.writeMediumLE(value);
	}

	@Nonnull
	@Override
	public Buffer writeInt(int value) {
		wrapped.writeInt(value);
		return this;
	}

	@Override
	public ByteBuf writeIntLE(int value) {
		return wrapped.writeIntLE(value);
	}

	@Nonnull
	@Override
	public Buffer writeLong(long value) {
		wrapped.writeLong(value);
		return this;
	}

	@Override
	public ByteBuf writeLongLE(long value) {
		return wrapped.writeLongLE(value);
	}

	@Override
	public ByteBuf writeChar(int value) {
		return wrapped.writeChar(value);
	}

	@Nonnull
	@Override
	public Buffer writeFloat(float value) {
		wrapped.writeFloat(value);
		return this;
	}

	@Override
	public ByteBuf writeFloatLE(float value) {
		return wrapped.writeFloatLE(value);
	}

	@Nonnull
	@Override
	public Buffer writeDouble(double value) {
		wrapped.writeDouble(value);
		return this;
	}

	@Override
	public ByteBuf writeDoubleLE(double value) {
		return wrapped.writeDoubleLE(value);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src) {
		return wrapped.writeBytes(src);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int length) {
		return wrapped.writeBytes(src, length);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
		return wrapped.writeBytes(src, srcIndex, length);
	}

	@Override
	public ByteBuf writeBytes(byte[] src) {
		return wrapped.writeBytes(src);
	}

	@Override
	public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
		return wrapped.writeBytes(src, srcIndex, length);
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer src) {
		return wrapped.writeBytes(src);
	}

	@Override
	public int writeBytes(InputStream in, int length) throws IOException {
		return wrapped.writeBytes(in, length);
	}

	@Override
	public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
		return wrapped.writeBytes(in, length);
	}

	@Override
	public int writeBytes(FileChannel in, long position, int length) throws IOException {
		return wrapped.writeBytes(in, position, length);
	}

	@Override
	public ByteBuf writeZero(int length) {
		return wrapped.writeZero(length);
	}

	@Override
	public int writeCharSequence(CharSequence sequence, Charset charset) {
		return wrapped.writeCharSequence(sequence, charset);
	}

	@Override
	public int indexOf(int fromIndex, int toIndex, byte value) {
		return wrapped.indexOf(fromIndex, toIndex, value);
	}

	@Override
	public int bytesBefore(byte value) {
		return wrapped.bytesBefore(value);
	}

	@Override
	public int bytesBefore(int length, byte value) {
		return wrapped.bytesBefore(length, value);
	}

	@Override
	public int bytesBefore(int index, int length, byte value) {
		return wrapped.bytesBefore(index, length, value);
	}

	@Override
	public int forEachByte(ByteProcessor processor) {
		return wrapped.forEachByte(processor);
	}

	@Override
	public int forEachByte(int index, int length, ByteProcessor processor) {
		return wrapped.forEachByte(index, length, processor);
	}

	@Override
	public int forEachByteDesc(ByteProcessor processor) {
		return wrapped.forEachByteDesc(processor);
	}

	@Override
	public int forEachByteDesc(int index, int length, ByteProcessor processor) {
		return wrapped.forEachByteDesc(index, length, processor);
	}

	@Override
	public ByteBuf copy() {
		return wrapped.copy();
	}

	@Override
	public ByteBuf copy(int index, int length) {
		return wrapped.copy(index, length);
	}

	@Override
	public ByteBuf slice() {
		return wrapped.slice();
	}

	@Override
	public ByteBuf retainedSlice() {
		return wrapped.retainedSlice();
	}

	@Override
	public ByteBuf slice(int index, int length) {
		return wrapped.slice(index, length);
	}

	@Override
	public ByteBuf retainedSlice(int index, int length) {
		return wrapped.retainedSlice(index, length);
	}

	@Override
	public ByteBuf duplicate() {
		return wrapped.duplicate();
	}

	@Override
	public ByteBuf retainedDuplicate() {
		return wrapped.retainedDuplicate();
	}

	@Override
	public int nioBufferCount() {
		return wrapped.nioBufferCount();
	}

	@Override
	public ByteBuffer nioBuffer() {
		return wrapped.nioBuffer();
	}

	@Override
	public ByteBuffer nioBuffer(int index, int length) {
		return wrapped.nioBuffer(index, length);
	}

	@Override
	public ByteBuffer internalNioBuffer(int index, int length) {
		return wrapped.internalNioBuffer(index, length);
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return wrapped.nioBuffers();
	}

	@Override
	public ByteBuffer[] nioBuffers(int index, int length) {
		return wrapped.nioBuffers(index, length);
	}

	@Override
	public boolean hasArray() {
		return wrapped.hasArray();
	}

	@Override
	public byte[] array() {
		return wrapped.array();
	}

	@Override
	public int arrayOffset() {
		return wrapped.arrayOffset();
	}

	@Override
	public boolean hasMemoryAddress() {
		return wrapped.hasMemoryAddress();
	}

	@Override
	public long memoryAddress() {
		return wrapped.memoryAddress();
	}

	@Override
	public String toString(Charset charset) {
		return wrapped.toString(charset);
	}

	@Override
	public String toString(int index, int length, Charset charset) {
		return wrapped.toString(index, length, charset);
	}

	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return wrapped.equals(obj);
	}

	@Override
	public int compareTo(ByteBuf buffer) {
		return wrapped.compareTo(buffer);
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}

	@Override
	public Buffer retain(int increment) {
		wrapped.retain(increment);
		return this;
	}

	@Override
	public Buffer retain() {
		wrapped.retain();
		return this;
	}

	@Override
	public Buffer touch() {
		wrapped.touch();
		return this;
	}

	@Override
	public Buffer touch(Object hint) {
		wrapped.touch(hint);
		return this;
	}

	@Override
	public int refCnt() {
		return wrapped.refCnt();
	}

	@Override
	public boolean release() {
		return wrapped.release();
	}

	@Override
	public boolean release(int decrement) {
		return wrapped.release(decrement);
	}
}
