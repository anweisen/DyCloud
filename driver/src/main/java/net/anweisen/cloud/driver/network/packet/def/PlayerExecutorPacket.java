package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerExecutorPacket extends Packet {

	public PlayerExecutorPacket(@Nonnull PlayerExecutorPayload payload, @Nonnull UUID playerUniqueId, @Nonnull Consumer<? super PacketBuffer> modifier) {
		super(PacketConstants.PLAYER_EXECUTOR_CHANNEL, newBuffer().writeEnum(payload).writeUniqueId(playerUniqueId));
		apply(modifier);
	}

	public enum PlayerExecutorPayload {
		SEND_MESSAGE,
		SEND_TRANSLATION,
		SEND_ACTIONBAR,
		SEND_TITLE,
		CONNECT_SERVER,
		CONNECT_FALLBACK,
		DISCONNECT
	}

}
