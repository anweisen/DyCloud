package net.anweisen.cloud.driver.player.permission.impl;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionManager;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultPermissionManager implements PermissionManager {

	protected final Map<UUID, PermissionGroup> groups = new LinkedHashMap<>();

	@Nonnull
	@Override
	public Collection<PermissionGroup> getGroups() {
		return Collections.unmodifiableCollection(groups.values());
	}

	@Nullable
	@Override
	public PermissionGroup getDefaultGroup() {
		return groups.values().stream().filter(PermissionGroup::isDefaultGroup).findFirst().orElse(null);
	}

	@Nullable
	@Override
	public PermissionGroup getHighestGroup() {
		return groups.values().stream().max(Comparator.comparingInt(PermissionGroup::getSortId)).orElse(null);
	}

	@Nullable
	@Override
	public PermissionGroup getGroupByUniqueId(@Nonnull UUID uniqueId) {
		return groups.get(uniqueId);
	}

	@Nullable
	@Override
	public PermissionGroup getGroupByName(@Nonnull String name) {
		return groups.values().stream().filter(group -> group.getName().equals(name)).findFirst().orElse(null);
	}

	@Override
	public void setGroupsCache(@Nonnull Collection<? extends PermissionGroup> groups) {
		this.groups.clear();
		for (PermissionGroup group : groups) {
			this.groups.put(group.getUniqueId(), group);
		}
	}

	@Nonnull
	@Override
	public PermissionPlayer getPlayer(@Nonnull CloudOfflinePlayer player) {
		return new DefaultPermissionPlayer(player);
	}

	@Nullable
	@Override
	public PermissionPlayer getPlayerByUniqueId(@Nonnull UUID uniqueId) {
		CloudOfflinePlayer player = CloudDriver.getInstance().getPlayerManager().getOfflinePlayerByUniqueId(uniqueId);
		return player == null ? null : getPlayer(player);
	}

	@Nullable
	@Override
	public PermissionPlayer getPlayerByName(@Nonnull String name) {
		CloudOfflinePlayer player = CloudDriver.getInstance().getPlayerManager().getOfflinePlayerByName(name);
		return player == null ? null : getPlayer(player);
	}

}
