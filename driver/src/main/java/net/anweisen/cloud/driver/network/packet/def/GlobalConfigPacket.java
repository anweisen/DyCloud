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

	public GlobalConfigPacket(@Nonnull GlobalConfigPayload payload) {
		super(PacketConstants.GLOBAL_CONFIG_CHANNEL, Buffer.create().writeEnumConstant(payload));
	}

	public GlobalConfigPacket(@Nonnull GlobalConfigPayload payload, @Nonnull Document data) {
		this(payload);
		buffer.writeDocument(data);
	}

	public enum GlobalConfigPayload {
		UPDATE,
		FETCH
	}

}
