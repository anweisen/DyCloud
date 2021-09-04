package net.anweisen.cloud.driver.player.defaults;

import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerManager;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultPlayerManager implements PlayerManager {

	protected final Map<UUID, CloudPlayer> onlinePlayers = new ConcurrentHashMap<>();

	@Override
	public int getOnlinePlayerCount() {
		return onlinePlayers.size();
	}

	@Nonnull
	@Override
	public Collection<CloudPlayer> getOnlinePlayers() {
		return Collections.unmodifiableCollection(onlinePlayers.values());
	}

	@Override
	public int getOnlineTaskPlayerCount(@Nonnull String taskName) {
		int count = 0;
		for (CloudPlayer player : onlinePlayers.values()) {
			if (player.getCurrentServer() != null && player.getCurrentServer().getTaskName().equals(taskName))
				count++;
		}
		return count;
	}

	@Nonnull
	@Override
	public Collection<CloudPlayer> getOnlineTaskPlayers(@Nonnull String taskName) {
		Collection<CloudPlayer> players = new LinkedList<>();
		for (CloudPlayer player : onlinePlayers.values()) {
			if (player.getCurrentServer() != null && player.getCurrentServer().getTaskName().equals(taskName))
				players.add(player);
		}
		return Collections.unmodifiableCollection(players);
	}

	@Nullable
	@Override
	public CloudPlayer getOnlinePlayerByName(@Nonnull String playerName) {
		for (CloudPlayer player : onlinePlayers.values()) {
			if (player.getName().equalsIgnoreCase(playerName))
				return player;
		}
		return null;
	}

	@Nullable
	@Override
	public CloudPlayer getOnlinePlayerByUniqueId(@Nonnull UUID uniqueId) {
		return onlinePlayers.get(uniqueId);
	}

	@Override
	public void setOnlinePlayerCache(@Nonnull Collection<? extends CloudPlayer> players) {
		onlinePlayers.clear();
		for (CloudPlayer player : players) {
			onlinePlayers.put(player.getUniqueId(), player);
		}
	}

	@Override
	public void registerPlayer(@Nonnull CloudPlayer player) {
		onlinePlayers.put(player.getUniqueId(), player);
	}

	@Override
	public void unregisterPlayer(@Nonnull UUID uniqueId) {
		onlinePlayers.remove(uniqueId);
	}

	@Override
	public long getRegisteredPlayerCount() {
		return getRegisteredPlayerCountAsync().getBeforeTimeout(15, TimeUnit.SECONDS);
	}

	@Nonnull
	@Override
	public Collection<CloudOfflinePlayer> getRegisteredPlayers() {
		return getRegisteredPlayersAsync().getBeforeTimeout(15, TimeUnit.SECONDS);
	}

	@Nullable
	@Override
	public CloudOfflinePlayer getOfflinePlayerByName(@Nonnull String playerName) {
		return getOfflinePlayerByNameAsync(playerName).getBeforeTimeout(15, TimeUnit.SECONDS);
	}

	@Nonnull
	@Override
	public Task<CloudOfflinePlayer> getOfflinePlayerByNameAsync(@Nonnull String playerName) {
		CloudPlayer onlinePlayer = getOnlinePlayerByName(playerName);
		if (onlinePlayer != null)
			return Task.completed(onlinePlayer);
		return getOfflinePlayerByNameAsync0(playerName);
	}

	@Nonnull
	protected abstract Task<CloudOfflinePlayer> getOfflinePlayerByNameAsync0(@Nonnull String playerName);

	@Nullable
	@Override
	public CloudOfflinePlayer getOfflinePlayerByUniqueId(@Nonnull UUID uniqueId) {
		return getOfflinePlayerByUniqueIdAsync(uniqueId).getBeforeTimeout(15, TimeUnit.SECONDS);
	}

	@Nonnull
	@Override
	public Task<CloudOfflinePlayer> getOfflinePlayerByUniqueIdAsync(@Nonnull UUID uniqueId) {
		CloudPlayer onlinePlayer = getOnlinePlayerByUniqueId(uniqueId);
		if (onlinePlayer != null)
			return Task.completed(onlinePlayer);
		return getOfflinePlayerByUniqueIdAsync0(uniqueId);
	}

	@Nonnull
	protected abstract Task<CloudOfflinePlayer> getOfflinePlayerByUniqueIdAsync0(@Nonnull UUID uniqueId);

}
