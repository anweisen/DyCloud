package net.anweisen.cloud.master.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.event.player.PlayerProxyLoginRequestEvent;
import net.anweisen.cloud.driver.network.NetworkingApiUser;
import net.anweisen.cloud.driver.network.packet.def.PlayerRemoteManagerPacket;
import net.anweisen.cloud.driver.network.packet.def.PlayerRemoteManagerPacket.PlayerRemoteManagerPayload;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerConstants;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.cloud.driver.player.connection.DefaultPlayerConnection;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudOfflinePlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultPlayerManager;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.translate.Translatable;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterPlayerManager extends DefaultPlayerManager implements LoggingApiUser, NetworkingApiUser {

	private final DatabaseManager databaseManager = CloudDriver.getInstance().getDatabaseManager();

	@Nonnull
	@Override
	public PlayerExecutor getPlayerExecutor(@Nonnull UUID playerUniqueId) {
		return new MasterPlayerExecutor(playerUniqueId);
	}

	@Nonnull
	@Override
	public PlayerExecutor getGlobalExecutor() {
		return MasterPlayerExecutor.GLOBAL;
	}

	@Nullable
	public CloudPlayer loginPlayer(@Nonnull UUID uniqueId, @Nonnull String name, @Nonnull DefaultPlayerConnection playerConnection, @Nonnull ServiceInfo serviceInfo,
                                   @Nonnull Consumer<String> cancelHandler) {
		info("Player[name={} uuid={}] requested to login on proxy '{}'", name, uniqueId, serviceInfo.getName());
		CloudPlayer player = getOnlinePlayerByUniqueId(uniqueId);

		final String cancelReason;
		if (player != null) {
			debug("Player[name={} uuid={}] is already connected to the network!", name, uniqueId);
			cancelReason = "§cAlready connected to the network";
		} else {
			CloudOfflinePlayer offlinePlayer = getOrCreateOfflinePlayer(uniqueId, name, playerConnection);
			if (CloudMaster.getInstance().getGlobalConfig().getMaintenance() && !offlinePlayer.getPermissionPlayer().hasPermission(Permissions.JOIN_MAINTENANCE)) {
				cancelReason = Translatable.of("cloud.kick.maintenance").translate(uniqueId).asString();
			} else if (CloudMaster.getInstance().getGlobalConfig().getMaxPlayers() > 0 && getOnlinePlayerCount() >= CloudMaster.getInstance().getGlobalConfig().getMaxPlayers() && !offlinePlayer.getPermissionPlayer().hasPermission(Permissions.JOIN_FULL)) {
				cancelReason = Translatable.of("cloud.kick.full").translate(uniqueId).asString();
			} else {
				player = registerOnlinePlayer(offlinePlayer, playerConnection, serviceInfo);
				PlayerProxyLoginRequestEvent event = CloudMaster.getInstance().getEventManager().callEvent(new PlayerProxyLoginRequestEvent(player));
				cancelReason = event.isCancelled() ? (event.getCancelledReason() != null ? event.getCancelledReason() : "§cNo kick reason given") : null;
			}
		}

		if (cancelReason != null) {
			cancelHandler.accept(cancelReason);
			return null;
		}

		return player;
	}

	@Nonnull
	public CloudOfflinePlayer getOrCreateOfflinePlayer(@Nonnull UUID uniqueId, @Nonnull String name, @Nonnull DefaultPlayerConnection playerConnection) {
		CloudOfflinePlayer offlinePlayer = getOfflinePlayerByUniqueId(uniqueId);
		if (offlinePlayer == null) {
			offlinePlayer = new DefaultCloudOfflinePlayer(
				uniqueId,
				name,
				"",
				playerConnection,
				new PermissionData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				System.currentTimeMillis(),
				System.currentTimeMillis(),
				0,
				Document.create()
			);
			saveOfflinePlayer(offlinePlayer);
			return offlinePlayer;
		}

		offlinePlayer.setLastOnlineTime(System.currentTimeMillis());
		offlinePlayer.setName(name);
		offlinePlayer.setLastConnection(playerConnection);
		saveOfflinePlayer(offlinePlayer);
		return offlinePlayer;
	}

	@Nonnull
	public CloudPlayer registerOnlinePlayer(@Nonnull CloudOfflinePlayer offlinePlayer, @Nonnull DefaultPlayerConnection connection, @Nonnull ServiceInfo proxy) {
		CloudPlayer player = new DefaultCloudPlayer(offlinePlayer, connection, proxy.getUniqueId());
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
	public void saveOfflinePlayer(@Nonnull CloudOfflinePlayer updatedPlayer) {
		if (onlinePlayers.containsKey(updatedPlayer.getUniqueId())) {
			CloudPlayer registeredPlayer = onlinePlayers.get(updatedPlayer.getUniqueId());
			sendPacket(new PlayerRemoteManagerPacket(PlayerRemoteManagerPayload.UPDATE_ONLINE_PLAYER, buffer -> buffer.writeObject((SerializableObject) registeredPlayer)));
		}

		debug("Saving {}..", updatedPlayer);
		databaseManager.getDatabase().insertOrUpdate(PlayerConstants.TABLE_NAME)
			.where(PlayerConstants.UUID_FIELD, updatedPlayer.getUniqueId())
			.set(PlayerConstants.NAME_FIELD, updatedPlayer.getName())
			.set(PlayerConstants.LANGUAGE_FIELD, updatedPlayer.getLanguage())
			.set(PlayerConstants.FIRST_LOGIN_TIME_FIELD, updatedPlayer.getFirstLoginTime())
			.set(PlayerConstants.LAST_ONLINE_TIME_FIELD, updatedPlayer.getLastOnlineTime())
			.set(PlayerConstants.ONLINE_DURATION_FIELD, updatedPlayer.getOnlineDuration())
			.set(PlayerConstants.LAST_CONNECTION_FIELD, Document.ofNullable(updatedPlayer.getLastConnection()))
			.set(PlayerConstants.PERMISSION_DATA_FIELD, Document.of(updatedPlayer.getStoredPermissionData()))
			.set(PlayerConstants.PROPERTIES_FIELD, updatedPlayer.getProperties())
			.executeAsync();
	}

	@Override
	public void deleteOfflinePlayer(@Nonnull UUID playerUniqueId) {
		databaseManager.getDatabase().delete(PlayerConstants.TABLE_NAME)
			.where(PlayerConstants.UUID_FIELD, playerUniqueId)
			.executeAsync();
	}

	@Override
	public void updateOnlinePlayer(@Nonnull CloudPlayer updatedPlayer) {
		if (!onlinePlayers.containsKey(updatedPlayer.getUniqueId())) throw new IllegalStateException("CloudPlayer is no longer online");

		onlinePlayers.put(updatedPlayer.getUniqueId(), updatedPlayer);
		sendPacket(new PlayerRemoteManagerPacket(PlayerRemoteManagerPayload.UPDATE_ONLINE_PLAYER, buffer -> buffer.writeObject((SerializableObject) updatedPlayer)));
	}

	@Nonnull
	protected CloudOfflinePlayer createOfflinePlayer(@Nonnull Document document) {
		return new DefaultCloudOfflinePlayer(
			document.getUUID(PlayerConstants.UUID_FIELD),
			document.getString(PlayerConstants.NAME_FIELD),
			document.getString(PlayerConstants.LANGUAGE_FIELD),
			document.getInstance(PlayerConstants.LAST_CONNECTION_FIELD, DefaultPlayerConnection.class),
			document.getInstance(PlayerConstants.PERMISSION_DATA_FIELD, PermissionData.class),
			document.getLong(PlayerConstants.FIRST_LOGIN_TIME_FIELD),
			document.getLong(PlayerConstants.LAST_ONLINE_TIME_FIELD),
			document.getLong(PlayerConstants.ONLINE_DURATION_FIELD),
			document.getDocument(PlayerConstants.PROPERTIES_FIELD)
		);
	}

}
