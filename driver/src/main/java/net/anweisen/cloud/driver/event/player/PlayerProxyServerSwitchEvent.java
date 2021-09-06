package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventType;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * Called when a player connected to another minecraft server
 *
 * Triggered by {@link PlayerEventType#PROXY_SERVER_SWITCH}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerProxyServerSwitchEvent extends PlayerProxyEvent {

	private final ServiceInfo from;
	private final ServiceInfo to;

	public PlayerProxyServerSwitchEvent(@Nonnull CloudPlayer player, @Nonnull ServiceInfo from, @Nonnull ServiceInfo to) {
		super(player);
		this.from = from;
		this.to = to;
	}

	@Nonnull
	public ServiceInfo getFrom() {
		return from;
	}

	@Nonnull
	public ServiceInfo getTo() {
		return to;
	}

	@Override
	public String toString() {
		return "PlayerProxyServerSwitchEvent[player=" + player + " from=" + from.getName() + " to=" + to.getName() + "]";
	}
}
