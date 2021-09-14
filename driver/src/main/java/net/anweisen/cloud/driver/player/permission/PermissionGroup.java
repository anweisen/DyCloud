package net.anweisen.cloud.driver.player.permission;

import net.anweisen.cloud.driver.CloudDriver;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PermissionGroup {

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	String getName();

	void setName(@Nonnull String name);

	@Nonnull
	default String getDisplayName() {
		return getColor() + getName();
	}

	@Nonnull
	String getColor();

	void setColor(@Nonnull String color);

	@Nonnull
	String getChatColor();

	void setChatColor(@Nonnull String color);

	@Nonnull
	String getNamePrefix();

	void setNamePrefix(@Nonnull String prefix);

	@Nonnull
	String getTabPrefix();

	void setTabPrefix(@Nonnull String prefix);

	int getSortId();

	void setSortId(int sortId);

	boolean isDefaultGroup();

	void setDefaultGroup(boolean defaultGroup);

	@Nonnull
	Collection<String> getInheritedGroups();

	void addInheritedGroup(@Nonnull String group);

	void removeInheritedGroup(@Nonnull String group);

	@Nonnull
	default Collection<PermissionGroup> findInheritedGroups() {
		return Collections.unmodifiableCollection(
			getInheritedGroups().stream().map(CloudDriver.getInstance().getPermissionManager()::getGroupByName).collect(Collectors.toList())
		);
	}

	@Nonnull
	Collection<String> getPermissions();

	void addPermission(@Nonnull String permission);

	void removePermission(@Nonnull String permission);

	boolean hasPermissionDirectly(@Nonnull String permission);

	@Nonnull
	Collection<String> getDeniedPermissions();

	void addDeniedPermission(@Nonnull String permission);

	void removeDeniedPermission(@Nonnull String permission);

	boolean hasDeniedPermissions(@Nonnull String permission);

	default boolean hasPermission(@Nonnull String permission) {
		if (hasPermissionDirectly("*"))
			return true;
		if (hasDeniedPermissions(permission))
			return false;
		if (hasPermissionDirectly(permission))
			return true;

		Collection<PermissionGroup> groups = findInheritedGroups();
		for (PermissionGroup group : groups) { // TODO may overflow
			if (group.hasPermission(permission))
				return true;
		}

		return false;
	}

	default void save() {
		CloudDriver.getInstance().getPermissionManager().saveGroup(this);
	}

}
