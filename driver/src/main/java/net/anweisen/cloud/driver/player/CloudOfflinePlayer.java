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

	/**
	 * @return the selected language or an empty string if no language is set
	 */
	@Nonnull
	String getLanguage();

	void setLanguage(@Nonnull String language);

	@Nonnull
	PlayerConnection getLastConnection();

	void setLastConnection(@Nonnull PlayerConnection connectionData);

	long getFirstLoginTime();

	void setFirstLoginTime(long time);

	long getLastOnlineTime();

	void setLastOnlineTime(long time);

	@Nonnull
	PermissionData getStoredPermissionData();

	@Nonnull
	Document getProperties();

	@Nonnull
	default PermissionPlayer getPermissionPlayer() {
		return CloudDriver.getInstance().getPermissionManager().getPlayer(this);
	}

	@Nullable
	default CloudPlayer getOnlinePlayer() {
		return CloudDriver.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(getUniqueId());
	}

	default boolean isOnline() {
		return getOnlinePlayer() != null;
	}

	default void save() {
		CloudDriver.getInstance().getPlayerManager().saveOfflinePlayer(this);
	}

}
