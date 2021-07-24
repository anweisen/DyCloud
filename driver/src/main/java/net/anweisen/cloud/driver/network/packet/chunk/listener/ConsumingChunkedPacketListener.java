package net.anweisen.cloud.driver.network.packet.chunk.listener;

import net.anweisen.cloud.driver.network.packet.chunk.ChunkedQueryResponse;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ConsumingChunkedPacketListener extends CachedChunkedPacketListener {

	private final Consumer<ChunkedQueryResponse> consumer;

	public ConsumingChunkedPacketListener(@Nonnull Consumer<ChunkedQueryResponse> consumer) {
		this.consumer = consumer;
	}

	@Override
	protected void handleComplete(@Nonnull ChunkedPacketSession session, @Nonnull InputStream inputStream) {
		consumer.accept(new ChunkedQueryResponse(session, session.getFirstPacket(), session.getLastPacket(), inputStream));
	}
}
