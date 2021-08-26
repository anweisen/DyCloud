package net.anweisen.cloud.driver.event.player;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.event.Cancelable;
import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventType;
import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Called when a player tries to connect to a proxy
 *
 * Triggered by {@link PlayerEventType#PROXY_LOGIN_REQUEST}
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
		Preconditions.checkArgument(CloudDriver.getInstance().getEnvironment() == DriverEnvironment.MASTER, "PlayerProxyLoginRequestEvent can only be cancelled from the master");
		this.cancelled = cancelled;
	}

	public void setCancelledReason(@Nullable String reason) {
		Preconditions.checkArgument(CloudDriver.getInstance().getEnvironment() == DriverEnvironment.MASTER, "PlayerProxyLoginRequestEvent can only be cancelled from the master");
		this.reason = reason;
		if (reason != null)
			setCancelled(true);
	}

	@Nullable
	public String getCancelledReason() {
		Preconditions.checkArgument(CloudDriver.getInstance().getEnvironment() == DriverEnvironment.MASTER, "PlayerProxyLoginRequestEvent can only be cancelled from the master");
		return reason;
	}
}
