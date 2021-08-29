package net.anweisen.cloud.driver.network.packet.chunk.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedPacket;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChunkedPacketSession implements LoggingApiUser {

	private final SocketChannel channel;
	private final ChunkedPacketListener listener;
	private final OutputStream outputStream;
	private final Collection<ChunkedPacket> pendingPackets = new ArrayList<>();
	private final Map<String, Object> properties;
	private final UUID sessionUniqueId;

	private ChunkedPacket firstPacket;
	private ChunkedPacket lastPacket;

	private int chunkId = 0;
	private Document header = Document.empty();
	private volatile boolean closed;

	public ChunkedPacketSession(@Nonnull SocketChannel channel, @Nonnull ChunkedPacketListener listener, @Nonnull UUID sessionUniqueId, @Nonnull OutputStream outputStream, @Nonnull Map<String, Object> properties) {
		this.channel = channel;
		this.listener = listener;
		this.sessionUniqueId = sessionUniqueId;
		this.outputStream = outputStream;
		this.properties = properties;
	}

	public void handleIncomingChunk(@Nonnull ChunkedPacket packet) throws IOException {
		if (this.closed) {
			packet.clearData();
			throw new IllegalStateException(String.format("Session is already closed but received packet %d, %b", packet.getChunkId(), packet.isEnd()));
		}

		if (packet.getChunkId() == 0 && this.header.isEmpty() && !packet.getHeader().isEmpty()) {
			this.header = packet.getHeader();
			this.firstPacket = packet;
		}

		if (packet.isEnd()) {
			this.lastPacket = packet;
		}

		try {
			if (this.chunkId != packet.getChunkId()) {
				this.pendingPackets.add(packet);
			} else {
				this.storeChunk(packet);
			}
		} finally {
			this.checkPendingPackets();
		}
	}

	private void storeChunk(@Nonnull ChunkedPacket packet) throws IOException {
		if (this.closed) {
			return;
		}

		if (packet.getChunkId() == 0) { // Ignore first packet because it has no data we need
			++chunkId;
			return;
		}

		if (packet.isEnd()) {
			close();
			return;
		}

		++chunkId;

		try {
			packet.readData(this.outputStream);
		} finally {
			this.outputStream.flush();
			packet.clearData();
		}
	}

	private void checkPendingPackets() throws IOException {
		if (!this.pendingPackets.isEmpty()) {
			Iterator<ChunkedPacket> iterator = this.pendingPackets.iterator();
			while (iterator.hasNext()) {
				ChunkedPacket pending = iterator.next();
				if (this.chunkId == pending.getChunkId() || (pending.isEnd() && this.chunkId - 1 == pending.getChunks())) {
					iterator.remove();
					this.storeChunk(pending);
				}
			}
		}
	}

	protected void close() throws IOException {
		if (!this.pendingPackets.isEmpty()) {
			String packets = this.pendingPackets.stream().map(ChunkedPacket::getChunkId).map(String::valueOf).collect(Collectors.joining(", "));
			throw new IllegalStateException(String.format("Closing with %d pending packets: %s", this.pendingPackets.size(), packets));
		}

		this.closed = true;
		this.outputStream.close();

		System.gc();

		trace("Closing session of ChunkedPacket consisting of {} chunks", chunkId+1);

		this.listener.getSessions().remove(this.sessionUniqueId);
		this.listener.handleComplete(this);
	}

	@Nonnull
	public SocketChannel getChannel() {
		return channel;
	}

	@Nonnull
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public ChunkedPacket getFirstPacket() {
		return firstPacket;
	}

	public ChunkedPacket getLastPacket() {
		return lastPacket;
	}

	public Document getHeader() {
		return header;
	}

	public boolean isClosed() {
		return closed;
	}

	@Nonnull
	public Map<String, Object> getProperties() {
		return properties;
	}

	public int getCurrentChunkId() {
		return chunkId;
	}
}
