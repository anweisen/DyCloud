package net.anweisen.cloud.driver.player.permission.impl;

import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultPermissionGroup implements PermissionGroup {

	private String name;
	private String color;
	private String prefix;
	private int sortId;
	private boolean defaultGroup;
	private Collection<String> groups;
	private Collection<String> permissions;
	private Collection<String> deniedPermissions;

	public DefaultPermissionGroup() {
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	@Override
	public String getColor() {
		return color;
	}

	@Override
	public void setColor(@Nonnull String color) {
		this.color = color;
	}

	@Nonnull
	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(@Nonnull String prefix) {
		this.prefix = prefix;
	}

	@Override
	public int getSortId() {
		return sortId;
	}

	@Override
	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	@Override
	public boolean isDefaultGroup() {
		return defaultGroup;
	}

	@Override
	public void setDefaultGroup(boolean defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	@Nonnull
	@Override
	public Collection<String> getInheritedGroups() {
		return Collections.unmodifiableCollection(groups);
	}

	@Override
	public void addInheritedGroup(@Nonnull String group) {
		groups.add(group);
	}

	@Override
	public void removeInheritedGroup(@Nonnull String group) {
		groups.remove(group);
	}

	@Nonnull
	@Override
	public Collection<String> getPermissions() {
		return Collections.unmodifiableCollection(permissions);
	}

	@Override
	public void addPermission(@Nonnull String permission) {
		permissions.add(permission);
	}

	@Override
	public void removePermission(@Nonnull String permission) {
		permissions.remove(permission);
	}

	@Nonnull
	@Override
	public Collection<String> getDeniedPermissions() {
		return Collections.unmodifiableCollection(deniedPermissions);
	}

	@Override
	public void addDeniedPermission(@Nonnull String permission) {
		deniedPermissions.add(permission);
	}

	@Override
	public void removeDeniedPermission(@Nonnull String permission) {
		deniedPermissions.remove(permission);
	}

	@Override
	public void save() {
		try {
			Document.of(this).saveToFile(CloudPermissionManager.directory.resolve(name + ".json"));
		} catch (IOException ex) {
			throw new WrappedException(ex);
		}
	}

	@Override
	public String toString() {
		return "PermissionGroup[name=" + name + " sortId=" + sortId + "defaultGroup=" + defaultGroup + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultPermissionGroup that = (DefaultPermissionGroup) o;
		return sortId == that.sortId
			&& defaultGroup == that.defaultGroup
			&& Objects.equals(name, that.name)
			&& Objects.equals(color, that.color)
			&& Objects.equals(prefix, that.prefix)
			&& Objects.equals(groups, that.groups)
			&& Objects.equals(permissions, that.permissions)
			&& Objects.equals(deniedPermissions, that.deniedPermissions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, color, prefix, sortId, defaultGroup, groups, permissions, deniedPermissions);
	}
}
