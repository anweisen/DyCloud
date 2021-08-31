package net.anweisen.cloud.driver.network.packet;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PacketListenerRegistry {

	private final Map<Integer, List<PacketListener>> listeners = new ConcurrentHashMap<>();

	public void addListener(int channel, @Nonnull PacketListener listener) {
		Preconditions.checkNotNull(listeners, "Listener cannot be null");
		addListeners(channel, listener);
	}

	public void addListeners(int channel, @Nonnull PacketListener... listeners) {
		Preconditions.checkNotNull(listeners, "Listeners cannot be null");

		List<PacketListener> list = this.listeners.computeIfAbsent(channel, key -> new CopyOnWriteArrayList<>());
		Collections.addAll(list, listeners);
	}

	public void removeListeners(int channel) {
		listeners.remove(channel);
	}

	public void clearListeners() {
		listeners.clear();
	}

	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) {
		List<PacketListener> listeners = this.listeners.get(packet.getChannel());
		if (listeners == null) return;

		for (PacketListener listener : listeners) {
			try {
				listener.handlePacket(channel, packet);
			} catch (Exception ex) {
				CloudDriver.getInstance().getLogger().error("An error occurred while handling packet {}", packet, ex);
			}
		}
	}

}
