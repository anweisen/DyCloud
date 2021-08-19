package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class AuthenticationPacket extends Packet {

	public AuthenticationPacket(@Nonnull AuthenticationType authenticationType, @Nonnull UUID identity, @Nonnull String name, @Nonnull Consumer<? super Buffer> modifier) {
		super(PacketConstants.AUTH_CHANNEL, Buffer.create().writeEnumConstant(authenticationType).writeUUID(identity).writeString(name));
		modifier.accept(body);
	}

	public enum AuthenticationType {
		NODE,
		CORD,
		SERVICE
	}

}
