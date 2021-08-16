package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerApiPacket.PlayerActionType;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * Called when a player tries to connect to another minecraft server
 *
 * Triggered by {@link PlayerActionType#PROXY_SERVER_CONNECT_REQUEST}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerProxyServerConnectRequestEvent extends PlayerProxyEvent {

	private final ServiceInfo target;

	public PlayerProxyServerConnectRequestEvent(@Nonnull CloudPlayer player, @Nonnull ServiceInfo target) {
		super(player);
		this.target = target;
	}

	@Nonnull
	public ServiceInfo getTarget() {
		return target;
	}
}
