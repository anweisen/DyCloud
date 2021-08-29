package net.anweisen.cloud.driver.network.packet.chunk;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
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

	protected ChunkedPacket(int channel, @Nonnull UUID uniqueId, @Nonnull Document header, Buffer body) {
		super(channel, uniqueId, header, body);
	}

	public static ChunkedPacket createIncomingPacket(int channel, @Nonnull UUID uniqueId, @Nonnull Document header, Buffer body) {
		return new ChunkedPacket(channel, uniqueId, header, body);
	}

	public ChunkedPacket fillBuffer() {
		if (body != null) { // The buffer is filled already
			return this;
		}

		body = Buffer.create().writeVarInt(chunkId);
		if (this.chunkId == 0) {
			body.writeInt(chunkSize);
			return this;
		}

		body.writeBoolean(end);
		if (this.end) {
			body.writeVarInt(chunks);
			return this;
		}

		body.writeInt(dataLength).writeBytes(data, 0, dataLength);
		return this;
	}

	@Nonnull
	public ChunkedPacket readBuffer() {
		chunkId = body.readVarInt();
		if (chunkId == 0) {
			chunkSize = body.readInt();
			return this;
		}

		end = body.readBoolean();
		if (end) {
			chunks = body.readVarInt();
		}

		return this;
	}

	public void readData(@Nonnull OutputStream outputStream) throws IOException {
		dataLength = body.readInt();
		body.readBytes(outputStream, dataLength);
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
		if (body != null) {
			while (body.refCnt() > 0) {
				body.release();
			}
		}

		data = null;
	}
}
