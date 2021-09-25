package net.anweisen.cloud.driver.player.permission.impl;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
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
	private String chatColor;
	private String tabPrefix;
	private String namePrefix;
	private int sortId;
	private boolean defaultGroup;
	private Collection<String> groups;
	private Collection<String> permissions;
	private Collection<String> deniedPermissions;

	private DefaultPermissionGroup() {
	}

	public DefaultPermissionGroup(@Nonnull String name, @Nonnull String color, @Nonnull String chatColor, @Nonnull String tabPrefix, @Nonnull String namePrefix,
	                              int sortId, boolean defaultGroup,
	                              @Nonnull Collection<String> groups, @Nonnull Collection<String> permissions, @Nonnull Collection<String> deniedPermissions) {
		Preconditions.checkNotNull(name, "name cannot be null");
		Preconditions.checkNotNull(color, "color cannot be null");
		Preconditions.checkNotNull(color, "chatColor cannot be null");
		Preconditions.checkNotNull(namePrefix, "namePrefix cannot be null");
		Preconditions.checkNotNull(tabPrefix, "tabPrefix cannot be null");
		Preconditions.checkArgument(sortId >= 0, "SortId cannot be negative");
		Preconditions.checkNotNull(groups, "groups cannot be null");
		Preconditions.checkNotNull(permissions, "permissions cannot be null");
		Preconditions.checkNotNull(deniedPermissions, "deniedPermissions cannot be null");

		this.uniqueId = UUID.randomUUID();
		this.name = name;
		this.color = color;
		this.chatColor = chatColor;
		this.tabPrefix = tabPrefix;
		this.namePrefix = namePrefix;
		this.sortId = sortId;
		this.defaultGroup = defaultGroup;
		this.groups = new ArrayList<>(groups);
		this.permissions = new ArrayList<>(permissions);
		this.deniedPermissions = new ArrayList<>(deniedPermissions);
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeUniqueId(uniqueId);
		buffer.writeString(name);
		buffer.writeString(color);
		buffer.writeString(chatColor);
		buffer.writeString(tabPrefix);
		buffer.writeString(namePrefix);
		buffer.writeVarInt(sortId);
		buffer.writeBoolean(defaultGroup);
		buffer.writeStringCollection(groups);
		buffer.writeStringCollection(permissions);
		buffer.writeStringCollection(deniedPermissions);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		uniqueId = buffer.readUniqueId();
		name = buffer.readString();
		color = buffer.readString();
		chatColor = buffer.readString();
		tabPrefix = buffer.readString();
		namePrefix = buffer.readString();
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
	public String getChatColor() {
		return chatColor;
	}

	@Override
	public void setChatColor(@Nonnull String chatColor) {
		this.chatColor = color;
	}

	@Nonnull
	@Override
	public String getNamePrefix() {
		return namePrefix;
	}

	@Override
	public void setNamePrefix(@Nonnull String prefix) {
		this.namePrefix = prefix;
	}

	@Nonnull
	@Override
	public String getTabPrefix() {
		return tabPrefix;
	}

	@Override
	public void setTabPrefix(@Nonnull String prefix) {
		this.tabPrefix = tabPrefix;
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
			&& Objects.equals(chatColor, that.chatColor)
			&& Objects.equals(namePrefix, that.namePrefix)
			&& Objects.equals(tabPrefix, that.tabPrefix)
			&& Objects.equals(groups, that.groups)
			&& Objects.equals(permissions, that.permissions)
			&& Objects.equals(deniedPermissions, that.deniedPermissions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, color, chatColor, namePrefix, tabPrefix, sortId, defaultGroup, groups, permissions, deniedPermissions);
	}
}
