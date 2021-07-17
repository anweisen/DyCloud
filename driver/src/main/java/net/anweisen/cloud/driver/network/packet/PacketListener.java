package net.anweisen.cloud.driver.network.packet;

import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PacketListener {

	void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception;

}
