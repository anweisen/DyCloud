package net.anweisen.cloud.driver.network.packet.chunk;

import net.anweisen.cloud.driver.network.packet.chunk.listener.ChunkedPacketSession;

import java.io.InputStream;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChunkedQueryResponse {

	private final ChunkedPacketSession session;
	private final ChunkedPacket beginPacket;
	private final ChunkedPacket endPacket;
	private final InputStream inputStream;

	public ChunkedQueryResponse(ChunkedPacketSession session, ChunkedPacket beginPacket, ChunkedPacket endPacket, InputStream inputStream) {
		this.session = session;
		this.beginPacket = beginPacket;
		this.endPacket = endPacket;
		this.inputStream = inputStream;
	}

	public ChunkedPacketSession getSession() {
		return this.session;
	}

	public ChunkedPacket getBeginPacket() {
		return this.beginPacket;
	}

	public ChunkedPacket getEndPacket() {
		return this.endPacket;
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}
}
