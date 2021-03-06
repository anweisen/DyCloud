package net.anweisen.cloud.driver.player.permission.impl;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.permission.PermissionData.PlayerGroupData;
import net.anweisen.cloud.driver.player.permission.PermissionData.TaskPermissionData;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultPermissionPlayer implements PermissionPlayer {

	private final CloudOfflinePlayer player;

	public DefaultPermissionPlayer(@Nonnull CloudOfflinePlayer player) {
		this.player = player;
	}

	@Nonnull
	@Override
	public CloudOfflinePlayer getPlayer() {
		return player;
	}

	@Nonnull
	@Override
	public String getName() {
		return player.getName();
	}

	@Nonnull
	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Nonnull
	@Override
	public Collection<String> getPermissions() {
		return player.getStoredPermissionData().getPermissions();
	}

	@Override
	public void addPermission(@Nonnull String permission) {
		player.getStoredPermissionData().getPermissions().add(permission);
	}

	@Override
	public void removePermission(@Nonnull String permission) {
		player.getStoredPermissionData().getPermissions().remove(permission);
	}

	@Override
	public boolean hasPermissionDirectly(@Nonnull String permission) {
		return player.getStoredPermissionData().getPermissions().contains(permission);
	}

	@Override
	public boolean testGroups() {
		long currentTime = System.currentTimeMillis();
		Collection<PlayerGroupData> groups = player.getStoredPermissionData().getGroups();
		int sizeBefore = groups.size();
		groups.removeIf(data -> data.getTimeoutTime() != -1 && currentTime > data.getTimeoutTime());
		if (sizeBefore != groups.size()) CloudDriver.getInstance().getLogger().trace("Removed timeouted groups from {}", player.getName());
		return sizeBefore != groups.size();
	}

	@Nonnull
	public Collection<PlayerGroupData> getPlayerGroups() {
		if (testGroups())
			save();

		return Collections.unmodifiableCollection(player.getStoredPermissionData().getGroups());
	}

	@Nonnull
	protected Stream<PermissionGroup> streamGroups() {
		return getPlayerGroups().stream()
			.map(PlayerGroupData::getUniqueId)
			.map(CloudDriver.getInstance().getPermissionManager()::getGroupByUniqueId)
			.filter(Objects::nonNull);
	}

	@Nonnull
	@Override
	public Collection<PermissionGroup> getGroups() {
		return Collections.unmodifiableCollection(streamGroups().collect(Collectors.toList()));
	}

	@Nullable
	@Override
	public PermissionGroup getHighestGroup() {
		return streamGroups().max(Comparator.comparingInt(PermissionGroup::getSortId)).orElse(CloudDriver.getInstance().getPermissionManager().getDefaultGroup());
	}

	@Override
	public void addGroup(@Nonnull String name) {
		addGroup(name, -1);
	}

	@Override
	public void addGroup(@Nonnull String name, long time, @Nonnull TimeUnit unit) {
		addGroup(name, System.currentTimeMillis() + unit.toMillis(time));
	}

	@Override
	public void addGroup(@Nonnull String name, long timeoutTimeMillis) {
		PermissionGroup group = CloudDriver.getInstance().getPermissionManager().getGroupByName(name);
		if (group != null)
			addGroup(group, timeoutTimeMillis);
	}

	@Override
	public void addGroup(@Nonnull PermissionGroup group) {
		addGroup(group, -1);
	}

	@Override
	public void addGroup(@Nonnull PermissionGroup group, long time, @Nonnull TimeUnit unit) {
		addGroup(group, System.currentTimeMillis() + unit.toMillis(time));
	}

	@Override
	public void addGroup(@Nonnull PermissionGroup group, long timeoutTimeMillis) {
		player.getStoredPermissionData().getGroups().add(new PlayerGroupData(group.getUniqueId(), timeoutTimeMillis));
	}

	@Override
	public void removeGroup(@Nonnull String name) {
		PermissionGroup group = CloudDriver.getInstance().getPermissionManager().getGroupByName(name);
		if (group != null)
			removeGroup(group);
	}

	@Override
	public void removeGroup(@Nonnull UUID uniqueId) {
		player.getStoredPermissionData().getGroups().removeIf(group -> group.getUniqueId().equals(uniqueId));
	}

	@Override
	public void removeGroup(@Nonnull PermissionGroup group) {
		removeGroup(group.getUniqueId());
	}

	@Override
	public boolean hasGroup(@Nonnull String name) {
		PermissionGroup group = CloudDriver.getInstance().getPermissionManager().getGroupByName(name);
		return group != null && hasGroup(group);
	}

	@Override
	public boolean hasGroup(@Nonnull UUID uniqueId) {
		for (PlayerGroupData group : player.getStoredPermissionData().getGroups()) {
			if (group.getUniqueId().equals(uniqueId))
				return true;
		}
		return false;
	}

	@Override
	public boolean hasGroup(@Nonnull PermissionGroup group) {
		return hasGroup(group.getUniqueId());
	}

	@Nonnull
	@Override
	public Map<String, Collection<String>> getTaskPermissions() {
		return player.getStoredPermissionData().getTaskPermissions().stream()
			.collect(Collectors.toMap(
					TaskPermissionData::getTask,
					permission -> new ArrayList<>(Collections.singletonList(permission.getName())),
					(o, o2) -> { o.addAll(o2); return o; },
					HashMap::new
				)
			);
	}

	@Override
	public boolean hasTaskPermission(@Nonnull String taskName, @Nonnull String permission) {
		return getTaskPermissions().getOrDefault(taskName, Collections.emptyList()).contains(permission);
	}

	@Override
	public void addTaskPermission(@Nonnull String taskName, @Nonnull String permission) {
		player.getStoredPermissionData().getTaskPermissions().add(new TaskPermissionData(permission, taskName));
	}

	@Override
	public void removeTaskPermission(@Nonnull String taskName, @Nonnull String permission) {
		player.getStoredPermissionData().getTaskPermissions().removeIf(data -> data.getTask().equals(taskName) && data.getName().equalsIgnoreCase(permission));
	}

	@Override
	public void removeTaskPermissions(@Nonnull String taskName) {
		player.getStoredPermissionData().getTaskPermissions().removeIf(data -> data.getName().equals(taskName));
	}

	@Override
	public void removeTaskPermissions() {
		player.getStoredPermissionData().getTaskPermissions().clear();
	}

	@Override
	public void save() {
		player.save();
	}

	@Override
	public String toString() {
		return "PermissionPlayer[name=" + player.getName() + " uuid=" + player.getUniqueId() + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultPermissionPlayer that = (DefaultPermissionPlayer) o;
		return Objects.equals(player, that.player);
	}

	@Override
	public int hashCode() {
		return Objects.hash(player);
	}
}
