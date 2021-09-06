package net.anweisen.cloud.driver.network.packet.chunk;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.exception.ChunkInterruptException;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChunkedPacketBuilder {

	public static final int DEFAULT_CHUNK_SIZE = 128 * 1024;

	private InputStream inputStream;
	private Integer channel;
	private Consumer<ChunkedPacket> target;

	private UUID uniqueId = UUID.randomUUID();
	private Document header = Document.empty();
	private int chunkSize = DEFAULT_CHUNK_SIZE;

	private boolean completed;
	private boolean success;

	public static ChunkedPacketBuilder newBuilder(int channel, InputStream inputStream) {
		return newBuilder().channel(channel).input(inputStream);
	}

	public static ChunkedPacketBuilder newBuilder() {
		return new ChunkedPacketBuilder();
	}

	public ChunkedPacketBuilder input(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	public ChunkedPacketBuilder input(Path path) throws IOException {
		return input(Files.newInputStream(path));
	}

	public ChunkedPacketBuilder channel(int channel) {
		this.channel = channel;
		return this;
	}

	public int channel() {
		return channel;
	}

	public ChunkedPacketBuilder target(Consumer<ChunkedPacket> target) {
		this.target = target;
		return this;
	}

	public ChunkedPacketBuilder target(@Nonnull SocketChannel channel) {
		return target(Collections.singletonList(channel));
	}

	public ChunkedPacketBuilder target(@Nonnull Collection<SocketChannel> channels) {
		return target(DefaultChunkedPacketHandler.createHandler(channels));
	}

	public ChunkedPacketBuilder uniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
		return this;
	}

	public UUID uniqueId() {
		return uniqueId;
	}

	public ChunkedPacketBuilder header(Document header) {
		this.header = header;
		return this;
	}

	public Document header() {
		return header;
	}

	public ChunkedPacketBuilder chunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
		return this;
	}

	public int chunkSize() {
		return chunkSize;
	}

	private ChunkedPacket createStartPacket(int channel, UUID uniqueId, Document header, int chunkSize) {
		return new ChunkedPacket(channel, uniqueId, header, 0, chunkSize, chunkSize, false, new byte[0], 0);
	}

	private ChunkedPacket createSegment(int channel, UUID uniqueId, int id, int chunkSize, int length, byte[] data) {
		return new ChunkedPacket(channel, uniqueId, Document.empty(), id, chunkSize, length, false, data, 0);
	}

	private ChunkedPacket createEndPacket(int channel, UUID uniqueId, int id, int chunkSize) {
		return new ChunkedPacket(channel, uniqueId, Document.empty(), id, chunkSize, 0, true, new byte[0], id - 1);
	}

	public ChunkedPacketBuilder complete() throws IOException {
		Preconditions.checkNotNull(inputStream, "No input provided");
		Preconditions.checkNotNull(target, "No handler provided");
		Preconditions.checkNotNull(channel, "No channel provided");
		Preconditions.checkArgument(!completed, "Builder cannot be completed twice");

		try {
			target.accept(createStartPacket(channel, uniqueId, header, chunkSize));

			int chunkId = 1;

			int read;
			byte[] buffer = new byte[chunkSize];
			while ((read = inputStream.read(buffer)) != -1) {
				target.accept(createSegment(channel, uniqueId, chunkId++, chunkSize, read, Arrays.copyOf(buffer, buffer.length)));
			}

			target.accept(createEndPacket(channel, uniqueId, chunkId, chunkSize));
			inputStream.close();

			success = true;
		} catch (ChunkInterruptException ex) {
		}

		completed = true;
		return this;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isSuccess() {
		return success;
	}

}
