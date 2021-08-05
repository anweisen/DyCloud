package net.anweisen.cloud.master.network.packets;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NetworkAuthResponsePacket extends Packet {

	public NetworkAuthResponsePacket(boolean access, @Nonnull String message) {
		super(PacketConstants.AUTH_CHANNEL, Document.newJsonDocument().set("access", access).set("message", message));
	}

}
