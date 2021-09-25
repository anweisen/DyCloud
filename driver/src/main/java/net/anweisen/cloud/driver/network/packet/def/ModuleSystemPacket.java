package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ModuleSystemPacket extends Packet {

	public ModuleSystemPacket(@Nonnull ModuleSystemPayload payload) {
		super(PacketConstants.MODULE_SYSTEM_CHANNEL, newBuffer().writeEnum(payload));
	}

	public ModuleSystemPacket(@Nonnull ModuleSystemPayload payload, int index) {
		this(payload);
		buffer.writeInt(index);
	}

	public enum ModuleSystemPayload {
		GET_MODULES,
		GET_MODULE_JAR,
		GET_MODULE_DATA_FOLDER
	}

}
