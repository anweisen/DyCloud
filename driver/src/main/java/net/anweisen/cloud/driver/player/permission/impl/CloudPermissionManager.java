package net.anweisen.cloud.driver.player.permission.impl;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.player.CloudOfflinePlayer;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionManager;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudPermissionManager implements PermissionManager, LoggingApiUser {

	static final Path directory = Paths.get("permissionGroups");

	private final Map<String, PermissionGroup> groups = new LinkedHashMap<>();

	public CloudPermissionManager() {
		FileUtils.createDirectory(directory);
	}

	@Override
	public void init() {
		for (Path file : FileUtils.list(directory).filter(path -> path.toString().endsWith(".json")).collect(Collectors.toList())) {
			extended("Loading permission group '{}'", file.getFileName());
			Document document = Document.readJsonFile(file);
			PermissionGroup group = document.toInstanceOf(DefaultPermissionGroup.class);
			extended("=> {}", group);
			if (group != null) groups.put(group.getName(), group);
		}
	}

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
	public PermissionGroup getGroupByName(@Nonnull String name) {
		return groups.get(name);
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
