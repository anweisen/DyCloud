package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see PlayerManager#getOfflinePlayerByUniqueId(UUID)
 * @see PlayerManager#getOfflinePlayerByName(String)
 */
public interface CloudOfflinePlayer {

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	String getName();

	void setName(@Nonnull String name);

	@Nonnull
	PlayerConnection getLastProxyConnectionData();

	void setLastProxyConnectionData(@Nonnull PlayerConnection connectionData);

	long getFirstLoginTime();

	long getLastOnlineTime();

	void setLastOnlineTime(long lastOnlineTime);

	@Nonnull
	PermissionData getStoredPermissionData();

	@Nonnull
	default PermissionPlayer getPermissionPlayer() {
		return CloudDriver.getInstance().getPermissionManager().getPlayer(this);
	}

	@Nullable
	default CloudPlayer getOnlinePlayer() {
		return CloudDriver.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(getUniqueId());
	}

	@Nonnull
	Document getProperties();

	default void save() {
		CloudDriver.getInstance().getPlayerManager().saveOfflinePlayer(this);
	}

}
