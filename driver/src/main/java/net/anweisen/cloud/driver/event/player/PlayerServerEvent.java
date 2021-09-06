package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class PlayerServerEvent extends PlayerEvent {

	protected final ServiceInfo service;

	public PlayerServerEvent(@Nonnull CloudPlayer player, @Nonnull ServiceInfo service) {
		super(player);
		this.service = service;
	}

	@Nonnull
	public ServiceInfo getService() {
		return service;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[player=" + player + " service=" + service.getName() + "]";
	}
}
