package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerExecutorPacket extends Packet {

	public PlayerExecutorPacket(@Nonnull PlayerExecutorType type, @Nonnull UUID playerUniqueId, @Nullable Consumer<? super Buffer> modifier) {
		super(PacketConstants.PLAYER_EXECUTOR_CHANNEL, Buffer.create().writeEnumConstant(type).writeUUID(playerUniqueId));
		if (modifier != null)
			modifier.accept(body);
	}

	public enum PlayerExecutorType {
		SEND_MESSAGE,
		SEND_ACTIONBAR,
		SEND_TITLE,
		CONNECT_SERVER,
		DISCONNECT
	}

}
