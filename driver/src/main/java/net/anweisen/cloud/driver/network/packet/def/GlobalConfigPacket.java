package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class GlobalConfigPacket extends Packet {

	public GlobalConfigPacket(@Nonnull GlobalConfigPacketType packetType) {
		super(PacketConstants.GLOBAL_CONFIG_CHANNEL, Buffer.create().writeEnumConstant(packetType));
	}

	public GlobalConfigPacket(@Nonnull GlobalConfigPacketType packetType, @Nonnull Document data) {
		this(packetType);
		body.writeDocument(data);
	}

	public enum GlobalConfigPacketType {
		UPDATE,
		FETCH,
	}

}
