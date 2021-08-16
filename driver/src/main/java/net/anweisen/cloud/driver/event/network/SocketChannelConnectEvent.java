package net.anweisen.cloud.driver.event.network;

import net.anweisen.cloud.driver.event.Cancelable;
import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SocketChannelConnectEvent extends SocketChannelEvent implements Cancelable {

	private boolean cancelled;

	public SocketChannelConnectEvent(@Nonnull SocketChannel channel) {
		super(channel);
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
