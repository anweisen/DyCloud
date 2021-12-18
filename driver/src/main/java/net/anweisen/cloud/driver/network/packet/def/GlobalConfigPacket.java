package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.utility.document.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class GlobalConfigPacket extends Packet {

	public GlobalConfigPacket(@Nonnull GlobalConfigPayload payload) {
		super(PacketChannels.GLOBAL_CONFIG_CHANNEL, newBuffer().writeEnum(payload));
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
