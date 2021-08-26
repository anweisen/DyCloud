package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerRemoteManagerPacket extends Packet {

	public PlayerRemoteManagerPacket(@Nonnull PlayerRemoteManagerType type, @Nullable Consumer<? super Buffer> modifier) {
		super(PacketConstants.PLAYER_REMOTE_MANAGER_CHANNEL, Buffer.create().writeEnumConstant(type));
		if (modifier != null)
			modifier.accept(body);
	}

	public enum PlayerRemoteManagerType {
		GET_REGISTERED_COUNT,
		GET_REGISTERED_PLAYERS,
		GET_OFFLINE_PLAYER_BY_NAME,
		GET_OFFLINE_PLAYER_BY_UUID,
		SAVE_OFFLINE_PLAYER
	}

}
