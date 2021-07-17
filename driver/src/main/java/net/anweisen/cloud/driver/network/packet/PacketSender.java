package net.anweisen.cloud.driver.network.packet;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PacketSender {

	void sendPacket(@Nonnull Packet packet);

	default void sendPackets(@Nonnull Packet... packets) {
		Preconditions.checkNotNull(packets);
		for (Packet packet : packets) {
			sendPacket(packet);
		}
	}

	void sendPacketSync(@Nonnull Packet packet);

	default void sendPacketsSync(@Nonnull Packet... packets) {
		Preconditions.checkNotNull(packets);
		for (Packet packet : packets) {
			sendPacketSync(packet);
		}
	}

}
