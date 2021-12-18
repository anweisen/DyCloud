package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.utility.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getPlayerManager()
 */
public interface PlayerManager {

	@Nonnull
	PlayerExecutor getPlayerExecutor(@Nonnull UUID playerUniqueId);

	@Nonnull
	PlayerExecutor getGlobalExecutor();

	int getOnlinePlayerCount();

	@Nonnull
	Collection<CloudPlayer> getOnlinePlayers();

	@Nonnull
	Collection<String> getOnlinePlayerNames();

	@Nonnull
	Collection<UUID> getOnlinePlayerUniqueIds();

	int getOnlineTaskPlayerCount(@Nonnull String taskName);

	@Nonnull
	Collection<CloudPlayer> getOnlineTaskPlayers(@Nonnull String taskName);

	@Nullable
	CloudPlayer getOnlinePlayerByName(@Nonnull String playerName);

	@Nullable
	CloudPlayer getOnlinePlayerByUniqueId(@Nonnull UUID uniqueId);

	void setOnlinePlayerCache(@Nonnull Collection<? extends CloudPlayer> players);

	void registerOnlinePlayer(@Nonnull CloudPlayer player);

	void unregisterOnlinePlayer(@Nonnull UUID uniqueId);

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

	void deleteOfflinePlayer(@Nonnull UUID playerUniqueId);

	default void deleteOfflinePlayer(@Nonnull CloudOfflinePlayer player) {
		deleteOfflinePlayer(player.getUniqueId());
	}

	void updateOnlinePlayer(@Nonnull CloudPlayer updatedPlayer);

}
