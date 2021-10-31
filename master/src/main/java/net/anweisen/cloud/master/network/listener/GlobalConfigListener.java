package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.global.GlobalConfigUpdatedEvent;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.GlobalConfigPacket.GlobalConfigPayload;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class GlobalConfigListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		GlobalConfigPayload payload = packet.getBuffer().readEnum(GlobalConfigPayload.class);
		switch (payload) {
			case FETCH: {
				CloudDriver.getInstance().getLogger().debug("GlobalConfigPayload.{} -> {}", CloudDriver.getInstance().getGlobalConfig().getRawData());
				channel.sendPacket(Packet.createResponseFor(packet,Packet.newBuffer().writeDocument(CloudDriver.getInstance().getGlobalConfig().getRawData())));
				break;
			}
			case UPDATE: {
				CloudDriver.getInstance().getGlobalConfig().setRawData(packet.getBuffer().readDocument());
				CloudDriver.getInstance().getLogger().debug("GlobalConfigPayload.{} -> {}", CloudDriver.getInstance().getGlobalConfig().getRawData());
				CloudDriver.getInstance().getEventManager().callEvent(new GlobalConfigUpdatedEvent());
				break;
			}
		}
	}
}
