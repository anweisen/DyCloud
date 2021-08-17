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

	@Nonnull
	Collection<CloudPlayer> getOnlinePlayers();

	@Nullable
	CloudPlayer getOnlinePlayerByName(@Nonnull String playerName);

	@Nullable
	CloudPlayer getOnlinePlayerByUniqueId(@Nonnull UUID uniqueId);

	@Nonnull
	Collection<CloudOfflinePlayer> getOfflinePlayers();

	@Nullable
	CloudOfflinePlayer getOfflinePlayerByName(@Nonnull String playerName);

	@Nullable
	CloudOfflinePlayer getOfflinePlayerByUniqueId(@Nonnull UUID uniqueId);

	void saveOfflinePlayer(@Nonnull CloudOfflinePlayer updatedPlayer);

}
