package net.anweisen.cloud.modules.proxy;

import net.anweisen.cloud.driver.network.packet.PacketConstants;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ProxyConstants {

	public enum ProxyPacketType {
		REQUEST_CONFIG,
		UPDATE_CONFIG
	}

	public static final int CHANNEL = 101;

	static {
		PacketConstants.registerChannelName("PROXY_MODULE_CHANNEL", CHANNEL);
	}

	private ProxyConstants() {}

}
