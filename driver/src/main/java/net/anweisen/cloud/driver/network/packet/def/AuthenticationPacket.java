package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class AuthenticationPacket extends Packet {

	public AuthenticationPacket(@Nonnull AuthenticationPayload payload, @Nonnull UUID identity, @Nonnull UUID id, @Nonnull Consumer<? super PacketBuffer> modifier) {
		super(PacketConstants.AUTH_CHANNEL, newBuffer().writeEnum(payload).writeUniqueId(identity).writeUniqueId(id));
		modifier.accept(buffer);
	}

	public AuthenticationPacket(@Nonnull AuthenticationPayload payload, @Nonnull UUID identity, @Nonnull String name, @Nonnull Consumer<? super PacketBuffer> modifier) {
		super(PacketConstants.AUTH_CHANNEL, newBuffer().writeEnum(payload).writeUniqueId(identity).writeString(name));
		modifier.accept(buffer);
	}

	public enum AuthenticationPayload {
		NODE,
		CORD,
		SERVICE
	}

}
