package net.anweisen.cloud.modules.proxy.helper;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.modules.proxy.ProxyConstants;
import net.anweisen.cloud.modules.proxy.ProxyConstants.ProxyPacketType;
import net.anweisen.cloud.modules.proxy.config.ProxyConfig;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ProxyHelper {

	public static ProxyConfig requestProxyConfig() {
		return CloudDriver.getInstance().getSocketComponent()
			.getFirstChannel().sendQuery(new Packet(ProxyConstants.CHANNEL, Buffer.create().writeEnumConstant(ProxyPacketType.REQUEST_CONFIG)))
			.getHeader().toInstanceOf(ProxyConfig.class);
	}

	public static void listenForProxyConfigUpdates(@Nonnull Consumer<? super ProxyConfig> updateHandler) {
		CloudDriver.getInstance().getSocketComponent().getListenerRegistry().addListener(ProxyConstants.CHANNEL, new ProxyConfigUpdateListener(updateHandler));
	}

	private ProxyHelper() {}
}
