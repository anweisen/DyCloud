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

	public ModuleSystemPacket(@Nonnull ModuleSystemRequestType type) {
		super(PacketConstants.MODULE_SYSTEM_CHANNEL, Buffer.create().writeEnumConstant(type));
	}

	public ModuleSystemPacket(@Nonnull ModuleSystemRequestType type, int count) {
		this(type);
		buffer.writeInt(count);
	}

	public enum ModuleSystemRequestType {
		GET_MODULES,
		GET_MODULE_JAR,
		GET_MODULE_DATA_FOLDER,
	}

}
