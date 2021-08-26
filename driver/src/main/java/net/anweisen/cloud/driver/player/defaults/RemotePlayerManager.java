package net.anweisen.cloud.driver.player.defaults;

import net.anweisen.cloud.driver.network.packet.def.PlayerRemoteManagerPacket;
import net.anweisen.cloud.driver.network.packet.def.PlayerRemoteManagerPacket.PlayerRemoteManagerType;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.network.request.NetworkingApiUser;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemotePlayerManager extends DefaultPlayerManager implements NetworkingApiUser {

	@Nonnull
	@Override
	public PlayerExecutor getPlayerExecutor(@Nonnull UUID playerUniqueId) {
		return new RemotePlayerExecutor(playerUniqueId);
	}

	@Nonnull
	@Override
	public Task<Integer> getRegisteredPlayerCountAsync() {
		return sendQueryAsync(new PlayerRemoteManagerPacket(PlayerRemoteManagerType.GET_REGISTERED_COUNT, null), buffer -> buffer.readInt());
	}

	@Nonnull
	@Override
	public Task<Collection<CloudOfflinePlayer>> getRegisteredPlayersAsync() {
		return sendQueryAsync(new PlayerRemoteManagerPacket(PlayerRemoteManagerType.GET_OFFLINE_PLAYERS, null), buffer -> buffer.readObjectCollection(DefaultCloudOfflinePlayer.class))
			.map(Collections::unmodifiableCollection);
	}

	@Nonnull
	@Override
	protected Task<CloudOfflinePlayer> getOfflinePlayerByNameAsync0(@Nonnull String playerName) {
		return sendQueryAsync(new PlayerRemoteManagerPacket(PlayerRemoteManagerType.GET_OFFLINE_PLAYER_BY_NAME, buffer -> buffer.writeString(playerName)), buffer -> buffer.readOptionalObject(DefaultCloudOfflinePlayer.class));
	}

	@Nonnull
	@Override
	protected Task<CloudOfflinePlayer> getOfflinePlayerByUniqueIdAsync0(@Nonnull UUID uniqueId) {
		return sendQueryAsync(new PlayerRemoteManagerPacket(PlayerRemoteManagerType.GET_OFFLINE_PLAYER_BY_UUID, buffer -> buffer.writeUUID(uniqueId)), buffer -> buffer.readOptionalObject(DefaultCloudOfflinePlayer.class));
	}

	@Override
	public void saveOfflinePlayer(@Nonnull CloudOfflinePlayer updatedPlayer) {
		DefaultCloudOfflinePlayer realOfflinePlayer;
		if (updatedPlayer instanceof DefaultCloudPlayer) {
			realOfflinePlayer = ((DefaultCloudPlayer)updatedPlayer).getOfflinePlayer();
		} else {
			realOfflinePlayer = (DefaultCloudOfflinePlayer) updatedPlayer;
		}

		sendPacket(new PlayerRemoteManagerPacket(PlayerRemoteManagerType.SAVE_OFFLINE_PLAYER, buffer -> buffer.writeObject(realOfflinePlayer)));
	}

}
