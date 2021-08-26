package net.anweisen.cloud.driver.player.permission.impl;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudPermissionManager extends DefaultPermissionManager implements LoggingApiUser {

//	private static final Path directory = Paths.get("permissionGroups");
	private static final Path file = Paths.get("groups.json");

	public CloudPermissionManager() {
//		FileUtils.createDirectory(directory);
		FileUtils.createFile(file);
	}

	@Override
	public void reload() {
//		for (Path file : FileUtils.list(directory).filter(path -> path.toString().endsWith(".json")).collect(Collectors.toList())) {
//			extended("Loading permission group '{}'", file.getFileName());
//			Document document = Document.readJsonFile(file);
//			PermissionGroup group = document.toInstanceOf(DefaultPermissionGroup.class);
//			extended("=> {}", group);
//			if (group != null) groups.put(group.getUniqueId(), group);
//		}
		for (Document document : Document.readJsonArrayFile(file)) {
			PermissionGroup group = document.toInstanceOf(DefaultPermissionGroup.class);
			extended("=> {}", group);
			if (group != null) groups.put(group.getUniqueId(), group);
		}
		saveGroups();
	}

	@Nonnull
	@Override
	public PermissionGroup createGroup(@Nonnull String name, @Nonnull String color, @Nonnull String prefix, int sortId, boolean defaultGroup,
	                                   @Nonnull Collection<String> groups, @Nonnull Collection<String> permissions, @Nonnull Collection<String> deniedPermissions) {
		return new DefaultPermissionGroup(name, color, prefix, sortId, defaultGroup, groups, permissions, deniedPermissions);
	}

	@Override
	public void removeGroup(@Nonnull UUID uniqueId) {
		groups.remove(uniqueId);
		// TODO publish
	}

	@Override
	public void saveGroup(@Nonnull PermissionGroup group) {
//		try {
//			Document.of(group).saveToFile(directory.resolve(group.getUniqueId() + ".json"));
//		} catch (IOException ex) {
//			throw new WrappedException(ex);
//		}
		saveGroups();
	}

	public void saveGroups() {
		List<PermissionGroup> groups = new ArrayList<>(this.groups.values());
		groups.sort(Comparator.comparingInt(PermissionGroup::getSortId));

		List<Document> documents = new ArrayList<>(groups.size());
		groups.forEach(group -> documents.add(Document.of(group)));

		try {
			Document.saveArray(documents, file);
		} catch (IOException ex) {
			throw new WrappedException(ex);
		}
	}

}