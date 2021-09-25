package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CommandSystemPacket extends Packet {

	public CommandSystemPacket(@Nonnull CommandSystemPayload payload, @Nonnull UUID player, @Nonnull String input) {
		super(PacketConstants.COMMAND_SYSTEM_CHANNEL, newBuffer().writeEnum(payload).writeUniqueId(player).writeString(input));
	}

	public CommandSystemPacket(@Nonnull CommandSystemPayload payload, @Nonnull Collection<String> commands) {
		super(PacketConstants.COMMAND_SYSTEM_CHANNEL, newBuffer().writeEnum(payload).writeStringCollection(commands));
	}

	public enum CommandSystemPayload {
		EXECUTE,
		COMPLETE,
		SET_COMMANDS,
		GET_COMMANDS
	}

}
