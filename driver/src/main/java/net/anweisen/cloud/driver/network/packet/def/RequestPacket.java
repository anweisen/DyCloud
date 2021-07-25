package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.request.RequestType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RequestPacket extends Packet {

	public RequestPacket(@Nonnull RequestType type, @Nullable Consumer<? super Buffer> modifier) {
		super(PacketConstants.REQUEST_API_CHANNEL, Buffer.create().writeEnumConstant(type));
		if (modifier != null) {
			modifier.accept(super.body);
		}
	}
}
