package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceUpdateSelfInfoPacket extends Packet { // TODO move to wrapper?

	public ServiceUpdateSelfInfoPacket(@Nonnull ServiceInfo info) {
		super(PacketChannels.SERVICE_UPDATE_SELF_INFO_CHANNEL, newBuffer().writeObject(info));
	}

}
