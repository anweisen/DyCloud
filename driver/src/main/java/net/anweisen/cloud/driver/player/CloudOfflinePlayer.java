package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.permission.PermissionData;
import net.anweisen.cloud.driver.player.permission.PermissionManager;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.cloud.driver.translate.TranslationManager;
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
	String getRawLanguage();

	/**
	 * @return the selected language or the {@link TranslationManager#getDefaultLanguage() default language} if none is selected
	 */
	@Nonnull
	String getLanguage();

	void setLanguage(@Nonnull String language);

	@Nonnull
	PlayerConnection getLastConnection();

	void setLastConnection(@Nonnull PlayerConnection connection);

	long getFirstLoginTime();

	void setFirstLoginTime(long time);

	long getLastOnlineTime();

	void setLastOnlineTime(long time);

	/**
	 * @return the total duration the player was on this network
	 */
	long getOnlineDuration();

	void setOnlineDuration(long duration);

	@Nonnull
	PermissionData getStoredPermissionData();

	@Nonnull
	Document getProperties();

	/**
	 * @return the {@link PermissionPlayer} instance returned by the {@link CloudDriver#getPermissionManager() PermissionManager}
	 *
	 * @throws IllegalStateException
	 *         If this driver does not have a {@link PermissionManager} set
	 */
	@Nonnull
	default PermissionPlayer getPermissionPlayer() {
		return CloudDriver.getInstance().getPermissionManager().getPlayer(this);
	}

	/**
	 * @return the current {@link CloudPlayer} instance, or {@code null} if the player is offline
	 */
	@Nullable
	default CloudPlayer getOnlinePlayer() {
		return CloudDriver.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(getUniqueId());
	}

	/**
	 * @return whether this player is currently online
	 */
	default boolean isOnline() {
		return getOnlinePlayer() != null;
	}

	/**
	 * Saves this {@link CloudOfflinePlayer} to the database using {@link PlayerManager#saveOfflinePlayer(CloudOfflinePlayer)}
	 */
	default void save() {
		CloudDriver.getInstance().getPlayerManager().saveOfflinePlayer(this);
	}

}
