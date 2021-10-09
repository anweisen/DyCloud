package net.anweisen.cloud.driver.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.anweisen.cloud.driver.network.packet.protocol.DefaultPacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.utilities.common.collection.WrappedException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyPacketBuffer extends DefaultPacketBuffer {

	protected final ByteBuf buffer;

	public NettyPacketBuffer(@Nonnull ByteBuf buffer) {
		this.buffer = buffer;
	}

	@Override
	public int length() {
		return buffer.readableBytes() + buffer.readerIndex();
	}

	@Override
	public int remaining() {
		return buffer.readableBytes();
	}

	@Override
	public boolean remain(int amount) {
		return remaining() >= amount;
	}

	@Nonnull
	@Override
	public byte[] asArray() {
		try {
			return buffer.array();
		} catch (Exception ex) {
		}

		byte[] bytes = new byte[buffer.readableBytes()];
		buffer.getBytes(buffer.readerIndex(), bytes);
		return bytes;
	}

	@Override
	public void read(@Nonnull byte[] bytes) {
		buffer.readBytes(bytes);
	}

	@Override
	public void read(@Nonnull OutputStream out, int length) throws IOException {
		buffer.readBytes(out, length);
	}

	@Nonnull
	@Override
	public PacketBuffer write(@Nonnull byte[] bytes) {
		buffer.writeBytes(bytes);
		return this;
	}

	@Nonnull
	@Override
	public PacketBuffer write(@Nonnull byte[] bytes, int index, int length) {
		buffer.writeBytes(bytes, index, length);
		return this;
	}

	@Override
	public boolean readBoolean() {
		return buffer.readBoolean();
	}

	@Nonnull
	@Override
	public PacketBuffer writeBoolean(boolean value) {
		buffer.writeBoolean(value);
		return this;
	}

	@Nonnull
	@Override
	public PacketBuffer writeByte(byte value) {
		buffer.writeByte(value);
		return this;
	}

	@Override
	public byte readByte() {
		return buffer.readByte();
	}

	@Override
	public int readInt() {
		return buffer.readInt();
	}

	@Nonnull
	@Override
	public PacketBuffer writeInt(int value) {
		buffer.writeInt(value);
		return this;
	}

	@Override
	public int readVarInt() {
		return NettyUtils.readVarInt(buffer);
	}

	@Nonnull
	@Override
	public PacketBuffer writeVarInt(int value) {
		NettyUtils.writeVarInt(buffer, value);
		return this;
	}

	@Override
	public long readLong() {
		return buffer.readLong();
	}

	@Nonnull
	@Override
	public PacketBuffer writeLong(long value) {
		buffer.writeLong(value);
		return this;
	}

	@Override
	public long readVarLong() {
		return NettyUtils.readVarLong(buffer);
	}

	@Nonnull
	@Override
	public PacketBuffer writeVarLong(long value) {
		NettyUtils.writeVarLong(buffer, value);
		return this;
	}

	@Override
	public float readFloat() {
		return buffer.readFloat();
	}

	@Nonnull
	@Override
	public PacketBuffer writeFloat(float value) {
		buffer.writeFloat(value);
		return this;
	}

	@Override
	public double readDouble() {
		return buffer.readDouble();
	}

	@Nonnull
	@Override
	public PacketBuffer writeDouble(double value) {
		buffer.writeDouble(value);
		return this;
	}

	@Override
	public char readChar() {
		return buffer.readChar();
	}

	@Nonnull
	@Override
	public PacketBuffer writeChar(char value) {
		buffer.writeChar(value);
		return this;
	}

	@Nonnull
	@Override
	public PacketBuffer writeThrowable(@Nonnull Throwable value) {
		try (ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer);
		     ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
			objectOutputStream.writeObject(value);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return this;
	}

	@Nonnull
	@Override
	public Throwable readThrowable() {
		try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer);
		     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
			return (Throwable) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@Override
	public PacketBuffer release() {
		buffer.release(buffer.refCnt());
		return this;
	}

	@Nonnull
	@Override
	public PacketBuffer copy() {
		return new NettyPacketBuffer(Unpooled.copiedBuffer(buffer));
	}
}
