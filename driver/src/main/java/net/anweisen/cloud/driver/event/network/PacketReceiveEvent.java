package net.anweisen.cloud.driver.event.network;

import net.anweisen.cloud.driver.event.Cancelable;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PacketReceiveEvent extends SocketChannelEvent implements Cancelable {

	private final Packet packet;

	private boolean cancelled;

	public PacketReceiveEvent(@Nonnull SocketChannel channel, @Nonnull Packet packet) {
		super(channel);
		this.packet = packet;
	}

	@Nonnull
	public Packet getPacket() {
		return packet;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
