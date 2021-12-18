package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utility.document.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see PlayerManager#getOnlinePlayerByUniqueId(UUID)
 * @see PlayerManager#getOnlinePlayerByName(String)
 */
public interface CloudPlayer extends CloudOfflinePlayer {

	@Nonnull
	PlayerConnection getConnection();

	@Nonnull
	PlayerSettings getSettings();

	void setSettings(@Nonnull PlayerSettings settings);

	@Nonnull
	ServiceInfo getProxy();

	@Nullable
	ServiceInfo getServer();

	@Nonnull
	Optional<ServiceInfo> getServerOptional();

	void setCurrentServer(@Nullable UUID server);

	/**
	 * @return the timestamp in millis the player joined the network
	 */
	long getJoinTime();

	/**
	 * @return the duration in millis this player is on the network (since he joined, not the total time)
	 *
	 * @see #getOnlineDuration()
	 */
	long getSessionDuration();

	/**
	 * @return whether this player is still online
	 */
	boolean isOnline();

	/**
	 * Marks this player instance as offline.
	 * {@link #isOnline()} will then return {@code false}.
	 */
	void setOffline();

	/**
	 * Custom properties which are not saved in the database (like {@link #getProperties()}) and are only available while the player is online.
	 *
	 * @return properties which are not saved in the database
	 *
	 * @see #getProperties()
	 */
	@Nonnull
	Document getOnlineProperties();

	/**
	 * Custom properties which are not saved in the database (like {@link ##setOnlineProperties(Document)}) and are only available while the player is online.
	 *
	 * @see #setOnlineProperties(Document)
	 */
	void setOnlineProperties(@Nonnull Document properties);

	@Nonnull
	default PlayerExecutor getExecutor() {
		return CloudDriver.getInstance().getPlayerManager().getPlayerExecutor(getUniqueId());
	}

	@Nonnull
	@Override
	@Deprecated
	default CloudPlayer getOnlinePlayer() {
		return this;
	}

	/**
	 * Synchronizes this player with the
	 */
	default void update() {
		CloudDriver.getInstance().getPlayerManager().updateOnlinePlayer(this);
	}

}
