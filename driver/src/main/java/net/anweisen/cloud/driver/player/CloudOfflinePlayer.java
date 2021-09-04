package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.data.PlayerProxyConnectionData;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CloudOfflinePlayer {

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	String getName();

	void setName(@Nonnull String name);

	@Nonnull
	PlayerProxyConnectionData getLastProxyConnectionData();

	void setLastProxyConnectionData(@Nonnull PlayerProxyConnectionData connectionData);

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
	default CloudPlayer findOnlinePlayer() {
		return CloudDriver.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(getUniqueId());
	}

	@Nonnull
	Document getProperties();

	default void save() {
		CloudDriver.getInstance().getPlayerManager().saveOfflinePlayer(this);
	}

}
