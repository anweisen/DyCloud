package net.anweisen.cloud.driver.network.packet.chunk.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedPacket;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class ChunkedPacketListener implements PacketListener {

	private final Lock lock = new ReentrantLock();
	private final Map<UUID, ChunkedPacketSession> sessions = new HashMap<>();

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		lock.lock();
		try {
			ChunkedPacket chunk = ChunkedPacket
				.createIncomingPacket(packet.getChannel(), packet.getUniqueId(), packet.getHeader(), packet.getBuffer())
				.readBuffer();
			if (!sessions.containsKey(packet.getUniqueId())) {
				sessions.put(packet.getUniqueId(), createSession(channel, packet.getUniqueId(), new HashMap<>()));
			}

			sessions.get(packet.getUniqueId()).handleIncomingChunk(chunk);
		} finally {
			lock.unlock();
		}
	}

	@Nonnull
	public Map<UUID, ChunkedPacketSession> getSessions() {
		return this.sessions;
	}

	@Nonnull
	protected ChunkedPacketSession createSession(@Nonnull SocketChannel channel, @Nonnull UUID sessionUniqueId, @Nonnull Map<String, Object> properties) throws IOException {
		return new ChunkedPacketSession(channel, this, sessionUniqueId, createOutputStream(sessionUniqueId, properties), properties);
	}

	@Nonnull
	protected abstract OutputStream createOutputStream(@Nonnull UUID sessionUniqueId, @Nonnull Map<String, Object> properties) throws IOException;

	protected void handleComplete(@Nonnull ChunkedPacketSession session) throws IOException {
	}

}
