package net.anweisen.cloud.driver.player.permission.impl;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultPermissionGroup implements PermissionGroup, SerializableObject {

	private UUID uniqueId;
	private String name;
	private String color;
	private String prefix;
	private int sortId;
	private boolean defaultGroup;
	private Collection<String> groups;
	private Collection<String> permissions;
	private Collection<String> deniedPermissions;

	private DefaultPermissionGroup() {
	}

	public DefaultPermissionGroup(@Nonnull String name, @Nonnull String color, @Nonnull String prefix, int sortId, boolean defaultGroup,
	                              @Nonnull Collection<String> groups, @Nonnull Collection<String> permissions, @Nonnull Collection<String> deniedPermissions) {
		Preconditions.checkNotNull(name, "Name cannot be null");
		Preconditions.checkNotNull(color, "Color cannot be null");
		Preconditions.checkNotNull(prefix, "Prefix cannot be null");
		Preconditions.checkArgument(sortId >= 0, "SortId cannot be negative");
		Preconditions.checkNotNull(groups, "Groups cannot be null");
		Preconditions.checkNotNull(permissions, "Permissions cannot be null");
		Preconditions.checkNotNull(deniedPermissions, "DeniedPermissions cannot be null");

		this.uniqueId = UUID.randomUUID();
		this.name = name;
		this.color = color;
		this.prefix = prefix;
		this.sortId = sortId;
		this.defaultGroup = defaultGroup;
		this.groups = new ArrayList<>(groups);
		this.permissions = new ArrayList<>(permissions);
		this.deniedPermissions = new ArrayList<>(deniedPermissions);
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(name);
		buffer.writeString(color);
		buffer.writeString(prefix);
		buffer.writeVarInt(sortId);
		buffer.writeBoolean(defaultGroup);
		buffer.writeStringCollection(groups);
		buffer.writeStringCollection(permissions);
		buffer.writeStringCollection(deniedPermissions);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		name = buffer.readString();
		color = buffer.readString();
		prefix = buffer.readString();
		sortId = buffer.readVarInt();
		defaultGroup = buffer.readBoolean();
		groups = buffer.readStringCollection();
		permissions = buffer.readStringCollection();
		deniedPermissions = buffer.readStringCollection();
	}

	@Nonnull
	@Override
	public UUID getUniqueId() {
		return uniqueId;
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

	@Override
	public boolean hasPermissionDirectly(@Nonnull String permission) {
		return permissions.contains(permission);
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
	public boolean hasDeniedPermissions(@Nonnull String permission) {
		return deniedPermissions.contains(permission);
	}

	@Override
	public String toString() {
		return "PermissionGroup[name=" + name + " sortId=" + sortId + " defaultGroup=" + defaultGroup + "]";
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
