package net.anweisen.cloud.driver.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerManager {

	int getOnlinePlayerCount();

	@Nonnull
	Collection<CloudPlayer> getOnlinePlayers();

	int getOnlineTaskPlayerCount(@Nonnull String taskName);

	@Nonnull
	Collection<CloudPlayer> getOnlineTaskPlayers(@Nonnull String taskName);

	@Nullable
	CloudPlayer getOnlinePlayerByName(@Nonnull String playerName);

	@Nullable
	CloudPlayer getOnlinePlayerByUniqueId(@Nonnull UUID uniqueId);

	long getRegisteredPlayerCount();

	@Nonnull
	Collection<CloudOfflinePlayer> getOfflinePlayers();

	@Nullable
	CloudOfflinePlayer getOfflinePlayerByName(@Nonnull String playerName);

	@Nullable
	CloudOfflinePlayer getOfflinePlayerByUniqueId(@Nonnull UUID uniqueId);

	void saveOfflinePlayer(@Nonnull CloudOfflinePlayer updatedPlayer);

}
