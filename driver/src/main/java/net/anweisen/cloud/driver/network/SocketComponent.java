package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;
import net.anweisen.cloud.driver.network.packet.PacketSender;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see SocketClient
 * @see SocketServer
 */
public interface SocketComponent extends PacketSender {

	void sendPacket(@Nonnull Packet packet, @Nonnull SocketChannel... skipChannels);

	void sendPacketSync(@Nonnull Packet packet, @Nonnull SocketChannel... skipChannels);

	@Nonnull
	Executor getPacketDispatcher();

	@Nonnull
	Collection<SocketChannel> getChannels();

	default SocketChannel getFirstChannel() {
		Collection<SocketChannel> channels = this.getChannels();
		return channels.isEmpty() ? null : channels.iterator().next();
	}

	void closeChannels();

	void shutdown();

	@Nonnull
	PacketListenerRegistry getListenerRegistry();

	@Nonnull
	@CheckReturnValue
	PacketBuffer newPacketBuffer();

}
