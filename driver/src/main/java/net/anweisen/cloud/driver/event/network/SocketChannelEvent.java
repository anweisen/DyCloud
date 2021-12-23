package net.anweisen.cloud.driver.event.network;

import net.anweisen.cloud.driver.event.DefaultEvent;
import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class SocketChannelEvent extends DefaultEvent {

	protected final SocketChannel channel;

	public SocketChannelEvent(@Nonnull SocketChannel channel) {
		this.channel = channel;
	}

	@Nonnull
	public SocketChannel getChannel() {
		return channel;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[channel=" + channel + "]";
	}
}
