package net.anweisen.cloud.driver.player.permission;

import net.anweisen.cloud.driver.player.CloudOfflinePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PermissionManager {

	void init();

	@Nonnull
	Collection<PermissionGroup> getGroups();

	@Nullable
	PermissionGroup getDefaultGroup();

	@Nullable
	PermissionGroup getHighestGroup();

	@Nullable
	PermissionGroup getGroupByName(@Nonnull String name);

	@Nonnull
	PermissionPlayer getPlayer(@Nonnull CloudOfflinePlayer player);

	@Nullable
	PermissionPlayer getPlayerByUniqueId(@Nonnull UUID uniqueId);

	@Nullable
	PermissionPlayer getPlayerByName(@Nonnull String name);

}
