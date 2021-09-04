package net.anweisen.cloud.master.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerConstants;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.cloud.driver.player.data.PlayerProxyConnectionData;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudOfflinePlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultPlayerManager;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterPlayerManager extends DefaultPlayerManager implements LoggingApiUser {

	private final DatabaseManager databaseManager = CloudDriver.getInstance().getDatabaseManager();

	@Nonnull
	@Override
	public PlayerExecutor getPlayerExecutor(@Nonnull UUID playerUniqueId) {
		return new MasterPlayerExecutor(playerUniqueId);
	}

	@Nonnull
	public CloudOfflinePlayer getOrCreateOfflinePlayer(@Nonnull PlayerProxyConnectionData playerConnection) {
		CloudOfflinePlayer offlinePlayer = getOfflinePlayerByUniqueId(playerConnection.getUniqueId());
		if (offlinePlayer == null) {
			offlinePlayer = new DefaultCloudOfflinePlayer(
				playerConnection.getUniqueId(),
				playerConnection.getName(),
				playerConnection,
				new PermissionData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				System.currentTimeMillis(),
				System.currentTimeMillis(),
				Document.create()
			);
			saveOfflinePlayer(offlinePlayer);
			return offlinePlayer;
		}

		offlinePlayer.setLastOnlineTime(System.currentTimeMillis());
		offlinePlayer.setName(playerConnection.getName());
		offlinePlayer.setLastProxyConnectionData(playerConnection);
		saveOfflinePlayer(offlinePlayer);
		return offlinePlayer;
	}

	@Nonnull
	public CloudPlayer registerOnlinePlayer(@Nonnull CloudOfflinePlayer offlinePlayer, @Nonnull ServiceInfo proxy) {
		CloudPlayer player = new DefaultCloudPlayer(offlinePlayer, offlinePlayer.getLastProxyConnectionData(), proxy);
		onlinePlayers.put(offlinePlayer.getUniqueId(), player);
		return player;
	}

//	@Nonnull
//	public CloudPlayer registerOnlinePlayer(@Nonnull PlayerProxyConnectionData playerConnection, @Nonnull ServiceInfo proxy) {
//		return registerOnlinePlayer(getOrCreateOfflinePlayer(playerConnection), proxy);
//	}

	@Nonnull
	@Override
	public Task<Integer> getRegisteredPlayerCountAsync() {
		return databaseManager.getDatabase().countEntries(PlayerConstants.TABLE_NAME).executeAsync().map(Long::intValue);
	}

	@Nonnull
	@Override
	public Task<Collection<CloudOfflinePlayer>> getRegisteredPlayersAsync() {
		return databaseManager.getDatabase().query(PlayerConstants.TABLE_NAME).executeAsync()
			.map(result -> result.all().map(this::createOfflinePlayer).collect(Collectors.toList()))
			.map(Collections::unmodifiableCollection);
	}

	@Nonnull
	@Override
	protected Task<CloudOfflinePlayer> getOfflinePlayerByNameAsync0(@Nonnull String playerName) {
		return databaseManager.getDatabase().query(PlayerConstants.TABLE_NAME)
			.where(PlayerConstants.NAME_FIELD, playerName, true)
			.executeAsync()
			.map(result -> result.first().map(this::createOfflinePlayer).orElse(null));
	}

	@Nonnull
	@Override
	protected Task<CloudOfflinePlayer> getOfflinePlayerByUniqueIdAsync0(@Nonnull UUID uniqueId) {
		return databaseManager.getDatabase().query(PlayerConstants.TABLE_NAME)
			.where(PlayerConstants.UUID_FIELD, uniqueId)
			.executeAsync()
			.map(result -> result.first().map(this::createOfflinePlayer).orElse(null));
	}

	@Override
	public void saveOfflinePlayer(@Nonnull CloudOfflinePlayer player) {
		debug("Saving {}..", player);
		databaseManager.getDatabase().insertOrUpdate(PlayerConstants.TABLE_NAME)
			.where(PlayerConstants.UUID_FIELD, player.getUniqueId())
			.set(PlayerConstants.NAME_FIELD, player.getName())
			.set(PlayerConstants.FIRST_LOGIN_TIME_FIELD, player.getFirstLoginTime())
			.set(PlayerConstants.LAST_ONLINE_TIME_FIELD, player.getLastOnlineTime())
			.set(PlayerConstants.LAST_CONNECTION_FIELD, Document.ofNullable(player.getLastProxyConnectionData()))
			.set(PlayerConstants.PERMISSION_DATA_FIELD, Document.of(player.getStoredPermissionData()))
			.set(PlayerConstants.PROPERTIES_FIELD, player.getProperties())
			.executeAsync();
	}

	@Nonnull
	protected CloudOfflinePlayer createOfflinePlayer(@Nonnull Document document) {
		return new DefaultCloudOfflinePlayer(
			document.getUUID(PlayerConstants.UUID_FIELD),
			document.getString(PlayerConstants.NAME_FIELD),
			document.get(PlayerConstants.LAST_CONNECTION_FIELD, PlayerProxyConnectionData.class),
			document.get(PlayerConstants.PERMISSION_DATA_FIELD, PermissionData.class),
			document.getLong(PlayerConstants.FIRST_LOGIN_TIME_FIELD),
			document.getLong(PlayerConstants.LAST_ONLINE_TIME_FIELD),
			document.getDocument(PlayerConstants.PROPERTIES_FIELD)
		);
	}

}
