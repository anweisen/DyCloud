package net.anweisen.cloud.driver.event.player;

import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerSettingsChangeEvent extends PlayerEvent {

	protected final PlayerSettings from;
	protected final PlayerSettings to;

	public PlayerSettingsChangeEvent(@Nonnull CloudPlayer player, @Nonnull PlayerSettings from, @Nonnull PlayerSettings to) {
		super(player);
		this.from = from;
		this.to = to;
	}

	@Nonnull
	public PlayerSettings getFrom() {
		return from;
	}

	@Nonnull
	public PlayerSettings getTo() {
		return to;
	}

	@Override
	public String toString() {
		return "PlayerSettingsChangeEvent[player=" + player + " settings=" + to + "]";
	}
}
