package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ModuleSystemPacket extends Packet {

	public ModuleSystemPacket(@Nonnull ModuleSystemPacketType type) {
		super(PacketConstants.MODULE_SYSTEM_CHANNEL, Buffer.create().writeEnumConstant(type));
	}

	public ModuleSystemPacket(@Nonnull ModuleSystemPacketType type, int index) {
		this(type);
		buffer.writeInt(index);
	}

	public enum ModuleSystemPacketType {
		GET_MODULES,
		GET_MODULE_JAR,
		GET_MODULE_DATA_FOLDER,
	}

}
