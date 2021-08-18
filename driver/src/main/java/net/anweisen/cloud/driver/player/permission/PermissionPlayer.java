package net.anweisen.cloud.driver.player.permission;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.permission.PermissionData.PlayerGroupData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PermissionPlayer {

	@Nonnull
	CloudOfflinePlayer getPlayer();

	@Nonnull
	String getName();

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	Collection<String> getPermissions();

	void addPermission(@Nonnull String permission);

	void removePermission(@Nonnull String permission);

	boolean hasPermissionDirectly(@Nonnull String permission);

	default boolean hasPermission(@Nonnull String permission) {
		if (hasPermissionDirectly(permission) || hasPermissionDirectly("*"))
			return true;

		PermissionGroup highestGroup = getHighestGroup();
		if (highestGroup == null)
			return false;

		return highestGroup.hasPermission(permission);
	}

	boolean testGroups();

	@Nonnull
	Collection<PlayerGroupData> getPlayerGroups();

	@Nonnull
	Collection<PermissionGroup> getGroups();

	@Nullable
	PermissionGroup getHighestGroup();

	@Nonnull
	default Optional<PermissionGroup> getHighestGroupOptional() {
		return Optional.ofNullable(getHighestGroup());
	}

	void addGroup(@Nonnull String name);

	void addGroup(@Nonnull String name, long time, @Nonnull TimeUnit unit);

	void addGroup(@Nonnull String name, long timeoutTimeMillis);

	void removeGroup(@Nonnull String name);

	boolean hasGroup(@Nonnull String name);

	@Nonnull
	Map<String, Collection<String>> getTaskPermissions();

	boolean hasTaskPermission(@Nonnull String taskName, @Nonnull String permission);

	void addTaskPermission(@Nonnull String taskName, @Nonnull String permission);

	void removeTaskPermission(@Nonnull String taskName, @Nonnull String permission);

	void removeTaskPermissions(@Nonnull String taskName);

	void removeTaskPermissions();

	void save();

}
