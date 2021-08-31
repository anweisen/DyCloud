package net.anweisen.cloud.driver.network;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedQueryResponse;
import net.anweisen.cloud.driver.network.packet.chunk.listener.ChunkedPacketListener;
import net.anweisen.cloud.driver.network.packet.chunk.listener.ConsumingChunkedPacketListener;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class InternalQueryResponseManager {

	private InternalQueryResponseManager() {}

	private static final Map<UUID, Callback> waitingPackets = new ConcurrentHashMap<>();

	public static boolean handleIncomingPacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) {
		Preconditions.checkNotNull(packet);

		if (!waitingPackets.containsKey(packet.getUniqueId()))
			return false;

		Callback callback = null;
		try {
			callback = waitingPackets.get(packet.getUniqueId());
			callback.action.accept(channel, packet);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		if (callback != null && callback.autoRemove) {
			waitingPackets.remove(packet.getUniqueId());
		}

		return true;
	}

	public static void removeEntry(@Nonnull UUID uniqueId) {
		waitingPackets.remove(uniqueId);
	}

	public static void registerQueryHandler(@Nonnull UUID uniqueId, @Nonnull Consumer<? super Packet> consumer) {
		registerQueryHandler(uniqueId, true, (channel, packet) -> consumer.accept(packet));
	}

	private static void registerQueryHandler(@Nonnull UUID uniqueId, boolean autoRemove, @Nonnull BiConsumer<? super SocketChannel, ? super Packet> consumer) {
		checkCachedValidation();
		waitingPackets.put(uniqueId, new Callback(autoRemove, consumer));
	}

	public static void registerChunkedQueryHandler(@Nonnull UUID uniqueId, @Nonnull Consumer<ChunkedQueryResponse> consumer) {
		ChunkedPacketListener listener = new ConsumingChunkedPacketListener(response -> {
			removeEntry(uniqueId);
			consumer.accept(response);
		});

		registerQueryHandler(uniqueId, false, (channel, packet) -> {
			try {
				listener.handlePacket(channel, packet);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	private static void checkCachedValidation() {
		long systemCurrent = System.currentTimeMillis();

		for (Entry<UUID, Callback> entry : waitingPackets.entrySet()) {
			if (entry.getValue().autoRemove && entry.getValue().timeout < systemCurrent) {
				waitingPackets.remove(entry.getKey());

				try {
					entry.getValue().action.accept(null, Packet.EMPTY_RESPONSE);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static final class Callback {

		private final long timeout = System.currentTimeMillis() + 60_000;
		private final boolean autoRemove;
		private final BiConsumer<? super SocketChannel, ? super Packet> action;

		public Callback(boolean autoRemove, @Nonnull BiConsumer<? super SocketChannel, ? super Packet> action) {
			this.autoRemove = autoRemove;
			this.action = action;
		}
	}

}
