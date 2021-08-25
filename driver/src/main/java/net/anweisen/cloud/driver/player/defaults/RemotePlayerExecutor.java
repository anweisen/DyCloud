package net.anweisen.cloud.driver.player.defaults;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemotePlayerExecutor extends DefaultPlayerExecutor {

	public RemotePlayerExecutor(@Nonnull UUID playerUniqueId) {
		super(playerUniqueId);
	}

	@Override
	protected void sendPacket(@Nonnull Packet packet) {
		CloudDriver.getInstance().getSocketComponent().sendPacket(packet);
	}

}
