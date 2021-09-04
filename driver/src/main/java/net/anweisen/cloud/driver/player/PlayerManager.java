package net.anweisen.cloud.driver.player;

import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerManager {

	@Nonnull
	PlayerExecutor getPlayerExecutor(@Nonnull UUID playerUniqueId);

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

	void setOnlinePlayerCache(@Nonnull Collection<? extends CloudPlayer> players);

	void registerPlayer(@Nonnull CloudPlayer player);

	void unregisterPlayer(@Nonnull UUID uniqueId);

	long getRegisteredPlayerCount();

	@Nonnull
	Task<Integer> getRegisteredPlayerCountAsync();

	@Nonnull
	Collection<CloudOfflinePlayer> getRegisteredPlayers();

	@Nonnull
	Task<Collection<CloudOfflinePlayer>> getRegisteredPlayersAsync();

	@Nullable
	CloudOfflinePlayer getOfflinePlayerByName(@Nonnull String playerName);

	@Nonnull
	Task<CloudOfflinePlayer> getOfflinePlayerByNameAsync(@Nonnull String playerName);

	@Nullable
	CloudOfflinePlayer getOfflinePlayerByUniqueId(@Nonnull UUID uniqueId);

	@Nonnull
	Task<CloudOfflinePlayer> getOfflinePlayerByUniqueIdAsync(@Nonnull UUID uniqueId);

	void saveOfflinePlayer(@Nonnull CloudOfflinePlayer updatedPlayer);

}
