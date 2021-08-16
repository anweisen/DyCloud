package net.anweisen.cloud.driver.event.network;

import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SocketChannelCloseEvent extends SocketChannelEvent {

	public SocketChannelCloseEvent(@Nonnull SocketChannel channel) {
		super(channel);
	}
}
