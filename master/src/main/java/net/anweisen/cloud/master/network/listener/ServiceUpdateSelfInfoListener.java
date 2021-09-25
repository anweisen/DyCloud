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
public class ServiceUpdateSelfInfoListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {

		ServiceInfo serviceInfo = packet.getBuffer().readObject(ServiceInfo.class);

		CloudMaster cloud = CloudMaster.getInstance();
		ServiceInfo oldServiceInfo = cloud.getServiceManager().getServiceInfoByUniqueId(serviceInfo.getUniqueId());
		if (!serviceInfo.equals(oldServiceInfo))
			cloud.publishUpdate(ServicePublishPayload.UPDATE, serviceInfo); // If the service info did not change we dont need to send the update and can save some traffic

		cloud.getServiceManager().handleServiceUpdate(ServicePublishPayload.UPDATE, serviceInfo);

	}

}
