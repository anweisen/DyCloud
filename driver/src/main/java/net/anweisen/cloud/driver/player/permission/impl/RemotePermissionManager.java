package net.anweisen.cloud.driver.player.permission.impl;

import net.anweisen.cloud.driver.player.permission.PermissionGroup;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemotePermissionManager extends DefaultPermissionManager {

	@Override
	public void reload() {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public PermissionGroup createGroup(@Nonnull String name, @Nonnull String color, @Nonnull String chatColor, @Nonnull String tabPrefix, @Nonnull String namePrefix, int sortId, boolean defaultGroup,
	                                   @Nonnull Collection<String> groups, @Nonnull Collection<String> permissions, @Nonnull Collection<String> deniedPermissions) {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeGroup(@Nonnull UUID uniqueId) {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveGroup(@Nonnull PermissionGroup group) {
		// TODO
		throw new UnsupportedOperationException();
	}

}
