package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceInfoPublishPacket extends Packet { // TODO move to base?

	public ServiceInfoPublishPacket(@Nonnull PublishType publishType, @Nonnull ServiceInfo info) {
		super(PacketConstants.SERVICE_INFO_PUBLISH_CHANNEL, Buffer.create().writeEnumConstant(publishType).writeObject(info));
	}

	public enum PublishType {
		UPDATE,
		STARTED,
		STOPPED,
		CONNECTED,
		DISCONNECTED,
		REGISTER,
		UNREGISTER
	}

}
