package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishPayload;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.CloudMaster;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceInfoUpdateListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {

		ServicePublishPayload payload = packet.getBuffer().readEnum(ServicePublishPayload.class);
		ServiceInfo info = packet.getBuffer().readObject(ServiceInfo.class);

		CloudMaster cloud = CloudMaster.getInstance();
		cloud.publishUpdate(payload, info, channel);
		cloud.getServiceManager().handleServiceUpdate(payload, info);

	}

}
