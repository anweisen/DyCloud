package net.anweisen.cloud.driver.player.permission.impl;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.utility.common.collection.WrappedException;
import net.anweisen.utility.common.misc.FileUtils;
import net.anweisen.utility.document.Bundle;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

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
		List<Document> documents = Documents.newJsonBundleUnchecked(file).toDocuments();
		for (Document document : documents) {
			PermissionGroup group = document.toInstance(DefaultPermissionGroup.class);
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

		Bundle bundle = Documents.newJsonBundle(groups.size());
		groups.forEach(group -> bundle.add(Documents.newJsonDocument(group)));

		try {
			bundle.saveToFile(file);
		} catch (IOException ex) {
			throw new WrappedException(ex);
		}
	}

}
