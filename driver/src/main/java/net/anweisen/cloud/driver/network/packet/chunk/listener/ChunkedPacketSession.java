package net.anweisen.cloud.driver.network.packet.chunk.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedPacket;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

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
	private Document header = Documents.emptyDocument();
	private volatile boolean closed;

	public ChunkedPacketSession(@Nonnull SocketChannel channel, @Nonnull ChunkedPacketListener listener, @Nonnull UUID sessionUniqueId, @Nonnull OutputStream outputStream, @Nonnull Map<String, Object> properties) {
		this.channel = channel;
		this.listener = listener;
		this.sessionUniqueId = sessionUniqueId;
		this.outputStream = outputStream;
		this.properties = properties;
	}

	public void handleIncomingChunk(@Nonnull ChunkedPacket packet) throws IOException {
		if (closed) {
			packet.clearData();
			throw new IllegalStateException(String.format("Session is already closed but received packet %d, %b", packet.getChunkId(), packet.isEnd()));
		}

		if (packet.getChunkId() == 0 && header.isEmpty() && !packet.getHeader().isEmpty()) {
			header = packet.getHeader();
			firstPacket = packet;
		}

		if (packet.isEnd()) {
			lastPacket = packet;
		}

		try {
			if (chunkId != packet.getChunkId()) {
				pendingPackets.add(packet);
			} else {
				storeChunk(packet);
			}
		} finally {
			checkPendingPackets();
		}
	}

	private void storeChunk(@Nonnull ChunkedPacket packet) throws IOException {
		if (closed) return;

		if (packet.getChunkId() == 0) { // Ignore first packet because it has no data we need
			chunkId++;
			return;
		}

		if (packet.isEnd()) {
			close();
			return;
		}

		chunkId++;

		try {
			packet.readData(outputStream);
		} finally {
			outputStream.flush();
			packet.clearData();
		}
	}

	private void checkPendingPackets() throws IOException {
		if (!pendingPackets.isEmpty()) {
			Iterator<ChunkedPacket> iterator = pendingPackets.iterator();
			while (iterator.hasNext()) {
				ChunkedPacket pending = iterator.next();
				if (chunkId == pending.getChunkId() || (pending.isEnd() && chunkId - 1 == pending.getChunks())) {
					iterator.remove();
					storeChunk(pending);
				}
			}
		}
	}

	protected void close() throws IOException {
		if (!pendingPackets.isEmpty()) {
			String packets = pendingPackets.stream().map(ChunkedPacket::getChunkId).map(String::valueOf).collect(Collectors.joining(", "));
			throw new IllegalStateException(String.format("Closing with %d pending packets: %s", pendingPackets.size(), packets));
		}

		closed = true;
		outputStream.close();

		System.gc();

		trace("Closing session of ChunkedPacket consisting of {} chunks: header:{}", chunkId + 1, header);

		listener.getSessions().remove(sessionUniqueId);
		listener.handleComplete(this);
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
