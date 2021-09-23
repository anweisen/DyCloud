package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerExecutorPacket extends Packet {

	public PlayerExecutorPacket(@Nonnull PlayerExecutorPacketType type, @Nonnull UUID playerUniqueId, @Nonnull Consumer<? super Buffer> modifier) {
		super(PacketConstants.PLAYER_EXECUTOR_CHANNEL, Buffer.create().writeEnumConstant(type).writeUUID(playerUniqueId));
		modifier.accept(buffer);
	}

	public enum PlayerExecutorPacketType {
		SEND_MESSAGE,
		SEND_ACTIONBAR,
		SEND_TITLE,
		CONNECT_SERVER,
		CONNECT_FALLBACK,
		DISCONNECT
	}

}
