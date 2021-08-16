package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.PublishType;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceInfoUpdateListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {

		PublishType publishType = packet.getBuffer().readEnumConstant(PublishType.class);
		ServiceInfo serviceInfo = packet.getBuffer().readObject(ServiceInfo.class);

		CloudDriver.getInstance().getServiceManager().handleServiceUpdate(publishType, serviceInfo);

	}

}
