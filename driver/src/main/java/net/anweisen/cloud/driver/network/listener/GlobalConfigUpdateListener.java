package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.global.GlobalConfigUpdatedEvent;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.GlobalConfigPacket.GlobalConfigPayload;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class GlobalConfigUpdateListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		GlobalConfigPayload payload = packet.getBuffer().readEnum(GlobalConfigPayload.class);
		switch (payload) {
			case UPDATE: {
				Document rawData = packet.getBuffer().readDocument();
				CloudDriver.getInstance().getGlobalConfig().setRawData(rawData);
				CloudDriver.getInstance().getEventManager().callEvent(new GlobalConfigUpdatedEvent());
				break;
			}
		}
	}
}
