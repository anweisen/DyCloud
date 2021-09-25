package net.anweisen.cloud.driver.network.packet.chunk;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChunkedPacket extends Packet {

	private int chunkId;
	private int chunkSize;
	private int dataLength;
	private boolean end;
	private byte[] data;
	private int chunks;

	protected ChunkedPacket(int channel, @Nonnull UUID uniqueId, @Nonnull Document header, int chunkId, int chunkSize,
	                        int dataLength, boolean end, byte[] data, int chunks) {
		super(channel, uniqueId, header);
		this.chunkId = chunkId;
		this.chunkSize = chunkSize;
		this.dataLength = dataLength;
		this.data = data;
		this.end = end;
		this.chunks = chunks;
	}

	protected ChunkedPacket(int channel, @Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull PacketBuffer buffer) {
		super(channel, uniqueId, header, buffer);
	}

	public static ChunkedPacket createIncomingPacket(int channel, @Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull PacketBuffer buffer) {
		return new ChunkedPacket(channel, uniqueId, header, buffer);
	}

	public ChunkedPacket fillBuffer() {
		if (buffer != null) { // The buffer is filled already
			return this;
		}

		buffer = newBuffer().writeVarInt(chunkId);
		if (this.chunkId == 0) {
			buffer.writeInt(chunkSize);
			return this;
		}

		buffer.writeBoolean(end);
		if (this.end) {
			buffer.writeVarInt(chunks);
			return this;
		}

		buffer.writeInt(dataLength).write(data, 0, dataLength);
		return this;
	}

	@Nonnull
	public ChunkedPacket readBuffer() {
		chunkId = buffer.readVarInt();
		if (chunkId == 0) {
			chunkSize = buffer.readInt();
			return this;
		}

		end = buffer.readBoolean();
		if (end) {
			chunks = buffer.readVarInt();
		}

		return this;
	}

	public void readData(@Nonnull OutputStream out) throws IOException {
		dataLength = buffer.readInt();
		buffer.read(out, dataLength);
	}

	public int getChunks() {
		return chunks;
	}

	public int getChunkId() {
		return chunkId;
	}

	public int getDataLength() {
		return dataLength;
	}

	public boolean isEnd() {
		return end;
	}

	public byte[] getData() {
		return data;
	}

	public void clearData() {
		if (buffer != null)
			buffer.release();
		data = null;
	}
}
