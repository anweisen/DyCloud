package net.anweisen.cloud.driver.player.permission;

import net.anweisen.cloud.driver.player.CloudOfflinePlayer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PermissionManager {

	void reload();

	@Nonnull
	Collection<PermissionGroup> getGroups();

	@Nullable
	PermissionGroup getDefaultGroup();

	@Nullable
	PermissionGroup getHighestGroup();

	@Nullable
	PermissionGroup getGroupByUniqueId(@Nonnull UUID uniqueId);

	@Nullable
	PermissionGroup getGroupByName(@Nonnull String name);

	// TODO comments
	@Nonnull
	@CheckReturnValue
	default PermissionGroup createGroup(@Nonnull String name, int sortId) {
		return createGroup(name, "", "", "", "", sortId);
	}

	@Nonnull
	@CheckReturnValue
	default PermissionGroup createGroup(@Nonnull String name, @Nonnull String color, @Nonnull String chatColor, @Nonnull String tabPrefix, @Nonnull String namePrefix, int sortId) {
		return createGroup(name, color, chatColor, tabPrefix, namePrefix, sortId, false, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
	}

	@Nonnull
	@CheckReturnValue
	PermissionGroup createGroup(@Nonnull String name, @Nonnull String color, @Nonnull String chatColor, @Nonnull String tabPrefix, @Nonnull String namePrefix, int sortId, boolean defaultGroup,
	                            @Nonnull Collection<String> groups, @Nonnull Collection<String> permissions, @Nonnull Collection<String> deniedPermissions);

	void setGroupsCache(@Nonnull Collection<? extends PermissionGroup> groups);

	void removeGroup(@Nonnull UUID uniqueId);

	default void removeGroup(@Nonnull String name) {
		PermissionGroup group = getGroupByName(name);
		if (group != null)
			removeGroup(group);
	}

	default void removeGroup(@Nonnull PermissionGroup group) {
		removeGroup(group.getUniqueId());
	}

	void saveGroup(@Nonnull PermissionGroup group);

	@Nonnull
	PermissionPlayer getPlayer(@Nonnull CloudOfflinePlayer player);

	@Nullable
	PermissionPlayer getPlayerByUniqueId(@Nonnull UUID uniqueId);

	@Nullable
	PermissionPlayer getPlayerByName(@Nonnull String name);

}
