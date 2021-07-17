package net.anweisen.cloud.driver.network.packet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.anweisen.cloud.driver.network.packet.SerializableObject;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class Buffer extends ByteBuf {

	public static final Buffer EMPTY = create();

	@Nonnull
	@CheckReturnValue
	public static Buffer create() {
		return wrap(Unpooled.buffer());
	}

//	@Nonnull
//	@CheckReturnValue
//	public static Buffer readAll(@Nonnull InputStream inputStream) {
//		return wrap(FileUtils.toByteArray(inputStream));
//	}

	@Nonnull
	@CheckReturnValue
	public static Buffer wrap(byte[] bytes) {
		return wrap(Unpooled.wrappedBuffer(bytes));
	}

	@Nonnull
	@CheckReturnValue
	public static Buffer wrap(@Nonnull ByteBuf buf) {
		return new DefaultBuffer(buf);
	}


	@Nonnull
	public abstract String readString();

	@Nonnull
	public abstract Buffer writeString(@Nonnull String stringToWrite);

	@Nullable
	public abstract String readOptionalString();

	public abstract Buffer writeOptionalString(@Nullable String stringToWrite);

	@Nonnull
	public abstract byte[] readArray();

	@Nonnull
	public abstract Buffer writeArray(@Nonnull byte[] bytes);

	@Nullable
	public abstract byte[] readOptionalArray();

	@Nonnull
	public abstract Buffer writeOptionalArray(@Nullable byte[] bytes);

	@Nonnull
	public abstract byte[] toArray();

	@Nonnull
	public abstract Collection<String> readStringCollection();

	public abstract Buffer writeStringCollection(@Nonnull Collection<String> list);

	@Nonnull
	public abstract String[] readStringArray();

	@Nonnull
	public abstract Buffer writeStringArray(@Nonnull String[] array);

	public abstract int readVarInt();

	@Nonnull
	public abstract Buffer writeVarInt(int value);

	public abstract long readVarLong();

	@Nonnull
	public abstract Buffer writeVarLong(long value);

	@Nonnull
	public abstract UUID readUUID();

	@Nonnull
	public abstract Buffer writeUUID(@Nonnull UUID uuid);

	@Nullable
	public abstract UUID readOptionalUUID();

	@Nonnull
	public abstract Buffer writeOptionalUUID(@Nullable UUID uuid);

	@Nonnull
	public abstract Collection<UUID> readUUIDCollection();

	@Nonnull
	public abstract Buffer writeUUIDCollection(@Nonnull Collection<UUID> uuids);

	@Nonnull
	public abstract Document readDocument();

	@Nonnull
	public abstract Buffer writeDocument(@Nonnull Document document);

	@Nullable
	public abstract Document readOptionalDocument();

	@Nonnull
	public abstract Buffer writeOptionalDocument(@Nullable Document document);

	@Nonnull
	public abstract Collection<Document> readDocumentCollection();

	@Nonnull
	public abstract Buffer writeDocumentCollection(@Nonnull Collection<? extends Document> documents);

	@Nonnull
	public abstract <T extends SerializableObject> T readObject(@Nonnull Class<T> objectClass);

	@Nonnull
	public abstract <T extends SerializableObject> T readObject(@Nonnull T targetObject);

	@Nonnull
	public abstract Buffer writeObject(@Nonnull SerializableObject object);

	@Nullable
	public abstract <T extends SerializableObject> T readOptionalObject(@Nonnull Class<T> objectClass);

	@Nullable
	public abstract <T extends SerializableObject> T readOptionalObject(@Nonnull T targetObject);

	@Nonnull
	public abstract Buffer writeOptionalObject(@Nullable SerializableObject object);

	@Nonnull
	public abstract <T extends SerializableObject> Collection<T> readObjectCollection(@Nonnull Class<T> objectClass);

	@Nonnull
	public abstract Buffer writeObjectCollection(@Nonnull Collection<? extends SerializableObject> objects);

	@Nonnull
	public abstract <T extends SerializableObject> T[] readObjectArray(@Nonnull Class<T> objectClass);

	@Nonnull
	public abstract <T extends SerializableObject> Buffer writeObjectArray(@Nonnull T[] objects);

	public abstract <E extends Enum<E>> E readEnumConstant(@Nonnull Class<E> enumClass);

	@Nonnull
	public abstract <E extends Enum<E>> Buffer writeEnumConstant(@Nonnull E enumConstant);

	public abstract <E extends Enum<E>> E readOptionalEnumConstant(@Nonnull Class<E> enumClass);

	@Nonnull
	public abstract <E extends Enum<E>> Buffer writeOptionalEnumConstant(@Nullable E enumConstant);

	@Nonnull
	public abstract Buffer writeThrowable(@Nullable Throwable throwable);

	@Nullable
	public abstract Throwable readThrowable();

	@Nonnull
	@Override
	public abstract Buffer writeBoolean(boolean value);

	@Nonnull
	@Override
	public abstract Buffer writeByte(int value);

	@Nonnull
	@Override
	public abstract Buffer writeShort(int value);

	@Nonnull
	@Override
	public abstract Buffer writeInt(int value);

	@Nonnull
	@Override
	public abstract Buffer writeLong(long value);

	@Nonnull
	@Override
	public abstract Buffer writeFloat(float value);

	@Nonnull
	@Override
	public abstract Buffer writeDouble(double value);

}
