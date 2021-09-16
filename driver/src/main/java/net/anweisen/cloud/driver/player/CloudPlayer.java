package net.anweisen.cloud.driver.player;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.config.Document;

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
	default PlayerExecutor getExecutor() {
		return CloudDriver.getInstance().getPlayerManager().getPlayerExecutor(getUniqueId());
	}

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

	long getJoinTime();

	boolean isOnline();

	void setOnline(boolean online);

	@Nonnull
	Document getOnlineProperties();

	void setOnlineProperties(@Nonnull Document properties);

	default void update() {
		CloudDriver.getInstance().getPlayerManager().updateOnlinePlayer(this);
	}

}
