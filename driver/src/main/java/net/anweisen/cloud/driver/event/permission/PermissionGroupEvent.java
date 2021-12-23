package net.anweisen.cloud.driver.event.permission;

import net.anweisen.cloud.driver.player.permission.PermissionGroup;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class PermissionGroupEvent extends PermissionEvent {

	protected final PermissionGroup group;

	public PermissionGroupEvent(@Nonnull PermissionGroup group) {
		this.group = group;
	}

	@Nonnull
	public PermissionGroup getGroup() {
		return group;
	}
}
