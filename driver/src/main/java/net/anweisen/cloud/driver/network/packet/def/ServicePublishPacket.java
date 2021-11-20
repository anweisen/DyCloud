package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServicePublishPacket extends Packet {

	public ServicePublishPacket(@Nonnull ServicePublishPayload payload, @Nonnull ServiceInfo info) {
		super(PacketChannels.SERVICE_INFO_PUBLISH_CHANNEL, newBuffer().writeEnum(payload).writeObject(info));
	}

	public enum ServicePublishPayload {
		UPDATE,
		STARTED,
		STOPPED,
		KILLED,
		RESTARTED,
		CONNECTED,
		DISCONNECTED,
		REGISTER,
		UNREGISTER
	}

}
