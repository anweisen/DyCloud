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
		if (super.body != null) { // The buffer is filled already
			return this;
		}

		super.body = Buffer.create().writeVarInt(this.chunkId);
		if (this.chunkId == 0) {
			super.body.writeInt(this.chunkSize);
			return this;
		}

		super.body.writeBoolean(this.end);
		if (this.end) {
			super.body.writeVarInt(this.chunks);
			return this;
		}

		super.body.writeInt(this.dataLength).writeBytes(this.data, 0, this.dataLength);
		return this;
	}

	@Nonnull
	public ChunkedPacket readBuffer() {
		this.chunkId = super.body.readVarInt();
		if (this.chunkId == 0) {
			this.chunkSize = super.body.readInt();
			return this;
		}

		this.end = super.body.readBoolean();
		if (this.end) {
			this.chunks = super.body.readVarInt();
		}

		return this;
	}

	public void readData(@Nonnull OutputStream outputStream) throws IOException {
		this.dataLength = this.body.readInt();
		this.body.readBytes(outputStream, this.dataLength);
	}

	public int getChunks() {
		return this.chunks;
	}

	public int getChunkId() {
		return this.chunkId;
	}

	public int getDataLength() {
		return this.dataLength;
	}

	public boolean isEnd() {
		return this.end;
	}

	public byte[] getData() {
		return this.data;
	}

	public void clearData() {
		if (super.body != null) {
			while (super.body.refCnt() > 0) {
				super.body.release();
			}
		}

		this.data = null;
	}
}
