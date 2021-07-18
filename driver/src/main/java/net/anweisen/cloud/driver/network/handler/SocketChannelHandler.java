package net.anweisen.cloud.driver.network.handler;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SocketChannelHandler {

	/**
	 * Handles an new open connected channel
	 *
	 * @param channel the providing channel on that this handler is sets on this
	 */
	void handleChannelInitialize(@Nonnull SocketChannel channel) throws Exception;

	/**
	 * Handles a incoming packet from a provided channel, that contains that channel handler
	 *
	 * @param channel the providing channel on that this handler is sets on this
	 * @param packet the packet, that was received from the remote component
	 * @return whether the packet that was received is allowed to handle from the packet listeners at the packetListenerRegistry
	 */
	boolean handlePacketReceive(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception;

	/**
	 * Handles the close phase from a NetworkChannel
	 *
	 * @param channel the providing channel on that this handler is sets on this
	 */
	void handleChannelClose(@Nonnull SocketChannel channel) throws Exception;

}
