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

	private static final Path file = Paths.get("groups.json");

	public CloudPermissionManager() {
		FileUtils.createFile(file);
	}

	@Override
	public void reload() {
		for (Document document : Document.readJsonArrayFile(file)) {
			PermissionGroup group = document.toInstanceOf(DefaultPermissionGroup.class);
			extended("=> {}", group);
			if (group != null) groups.put(group.getUniqueId(), group);
		}
		saveGroups();
	}

	@Nonnull
	@Override
	public PermissionGroup createGroup(@Nonnull String name, @Nonnull String color, @Nonnull String chatColor, @Nonnull String tabPrefix, @Nonnull String namePrefix, int sortId, boolean defaultGroup,
	                                   @Nonnull Collection<String> groups, @Nonnull Collection<String> permissions, @Nonnull Collection<String> deniedPermissions) {
		return new DefaultPermissionGroup(name, color, chatColor, tabPrefix, namePrefix, sortId, defaultGroup, groups, permissions, deniedPermissions);
	}

	@Override
	public void removeGroup(@Nonnull UUID uniqueId) {
		groups.remove(uniqueId);
		// TODO publish
	}

	@Override
	public void saveGroup(@Nonnull PermissionGroup group) {
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