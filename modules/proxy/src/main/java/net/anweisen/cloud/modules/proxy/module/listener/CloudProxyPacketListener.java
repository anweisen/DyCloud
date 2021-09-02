package net.anweisen.cloud.modules.proxy.module.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.modules.proxy.CloudProxyModule;
import net.anweisen.cloud.modules.proxy.ProxyConstants.ProxyPacketType;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudProxyPacketListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		ProxyPacketType type = packet.getBuffer().readEnumConstant(ProxyPacketType.class);
		switch (type) {
			case REQUEST_CONFIG: {
				channel.sendPacket(Packet.createResponseFor(packet, Document.create().set(CloudProxyModule.getInstance().getProxyConfig())));
				break;
			}
		}
	}
}
