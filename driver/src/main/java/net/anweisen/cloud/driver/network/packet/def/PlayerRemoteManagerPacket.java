package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerRemoteManagerPacket extends Packet {

	public PlayerRemoteManagerPacket(@Nonnull PlayerRemoteManagerPayload payload, @Nullable Consumer<? super PacketBuffer> modifier) {
		super(PacketChannels.PLAYER_REMOTE_MANAGER_CHANNEL, newBuffer().writeEnum(payload));
		apply(modifier);
	}

	public enum PlayerRemoteManagerPayload {
		GET_REGISTERED_COUNT,
		GET_REGISTERED_PLAYERS,
		GET_OFFLINE_PLAYER_BY_NAME,
		GET_OFFLINE_PLAYER_BY_UUID,
		SAVE_OFFLINE_PLAYER,
		DELETE_OFFLINE_PLAYER,
		UPDATE_ONLINE_PLAYER
	}

}
