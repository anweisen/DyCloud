package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class TemplateStoragePacket extends Packet {

	public TemplateStoragePacket(@Nonnull TemplateStoragePayload payload, @Nonnull Consumer<? super Buffer> modifier) {
		super(PacketConstants.TEMPLATE_STORAGE_CHANNEL, Buffer.create().writeEnumConstant(payload));
		modifier.accept(buffer);
	}

	public enum TemplateStoragePayload {
		GET_TEMPLATES,
		HAS_TEMPLATE,
		LOAD_TEMPLATE_STREAM
	}
}
