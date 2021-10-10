package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class TranslationSystemPacket extends Packet {

	public TranslationSystemPacket(@Nonnull TranslationPayload payload, @Nullable Consumer<? super PacketBuffer> modifier) {
		super(PacketConstants.TRANSLATION_SYSTEM_CHANNEL, newBuffer().writeEnum(payload));
		apply(modifier);
	}

	public enum TranslationPayload {
		GET_SECTION,
		RETRIEVE
	}
}
