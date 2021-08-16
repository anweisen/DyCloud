package net.anweisen.cloud.driver.event.network;

import net.anweisen.cloud.driver.event.Event;
import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class SocketChannelEvent implements Event {

	private final SocketChannel channel;

	private boolean cancelled;

	public SocketChannelEvent(@Nonnull SocketChannel channel) {
		this.channel = channel;
	}

	@Nonnull
	public SocketChannel getChannel() {
		return channel;
	}

}
