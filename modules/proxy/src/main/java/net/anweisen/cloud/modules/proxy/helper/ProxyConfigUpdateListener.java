package net.anweisen.cloud.modules.proxy.helper;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.modules.proxy.ProxyConstants.ProxyPacketType;
import net.anweisen.cloud.modules.proxy.config.ProxyConfig;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyConfigUpdateListener implements PacketListener {

	private final Consumer<? super ProxyConfig> updateHandler;

	public ProxyConfigUpdateListener(@Nonnull Consumer<? super ProxyConfig> updateHandler) {
		this.updateHandler = updateHandler;
	}

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		ProxyPacketType type = packet.getBuffer().readEnumConstant(ProxyPacketType.class);
		switch (type) {
			case UPDATE_CONFIG: {
				updateHandler.accept(packet.getHeader().toInstanceOf(ProxyConfig.class));
				break;
			}
		}
	}
}
