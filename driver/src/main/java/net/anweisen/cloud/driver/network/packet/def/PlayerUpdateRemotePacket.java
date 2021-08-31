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
public class PlayerUpdateRemotePacket extends Packet {

	public PlayerUpdateRemotePacket(@Nonnull PlayerUpdateType type, @Nullable Consumer<? super Buffer> modifier) {
		super(PacketConstants.PLAYER_UPDATE_REMOTE_CHANNEL, Buffer.create().writeEnumConstant(type));
		if (modifier != null)
			modifier.accept(body);
	}

	public enum PlayerUpdateType {

	}

}
