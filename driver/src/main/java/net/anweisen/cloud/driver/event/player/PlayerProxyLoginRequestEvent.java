package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.event.Cancelable;
import net.anweisen.cloud.driver.network.packet.def.PlayerApiPacket.PlayerActionType;
import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Called when a player tries to connect to a proxy
 *
 * Triggered by {@link PlayerActionType#PROXY_LOGIN_REQUEST}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerProxyLoginRequestEvent extends PlayerProxyEvent implements Cancelable {

	private boolean cancelled;
	private String reason;

	public PlayerProxyLoginRequestEvent(@Nonnull CloudPlayer player) {
		super(player);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public void setCancelledReason(@Nullable String reason) {
		this.reason = reason;
	}

	@Nullable
	public String getCancelledReason() {
		return reason;
	}
}
