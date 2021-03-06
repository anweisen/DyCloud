package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.network.packet.def.PlayerEventPacket.PlayerEventPayload;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Triggered by {@link PlayerEventPayload#SERVER_DISCONNECT}
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerServerDisconnectEvent extends PlayerServerEvent {

	protected final UUID uuid;

	public PlayerServerDisconnectEvent(@Nullable CloudPlayer player, @Nonnull ServiceInfo service, @Nonnull UUID uuid) {
		super(player, service);
		this.uuid = uuid;
	}

	@Nonnull
	@Override
	public UUID getPlayerUniqueId() {
		return uuid;
	}

	/**
	 * @return the online player or {@code null} if the player was already disconnected from the proxy (disconnected from the proxy and got disconnected from the server for that reason)
	 */
	@Nullable
	@Override
	public CloudPlayer getPlayer() {
		return super.getPlayer();
	}

	@Override
	public String toString() {
		return "PlayerServerDisconnectEvent[player=CloudPlayer[uuid=" + uuid + "] service=" + service.getName() + "]";
	}
}
