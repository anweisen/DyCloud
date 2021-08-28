package net.anweisen.cloud.modules.perms.bukkit;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BukkitCloudPermsPermissible extends PermissibleBase {

	private final Player player;

	private PermissionPlayer permissionPlayer;

	public BukkitCloudPermsPermissible(@Nonnull Player player) {
		super(player);
		this.player = player;
	}

	@Nonnull
	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		Set<PermissionAttachmentInfo> permissions = new HashSet<>();
		if (getPermissionPlayer() == null) return permissions;

		for (String permission : permissionPlayer.getPermissions()) {
			permissions.add(new PermissionAttachmentInfo(this, permission, null, true));
		}
		for (Entry<String, Collection<String>> entry : permissionPlayer.getTaskPermissions().entrySet()) {
			for (String permission : entry.getValue()) {
				permissions.add(new PermissionAttachmentInfo(this, permission, null, true));
			}
		}
		for (PermissionGroup group : permissionPlayer.getGroups()) {
			for (String permission : group.getPermissions()) {
				permissions.add(new PermissionAttachmentInfo(this, permission, null, true));
			}
			for (String permission : group.getDeniedPermissions()) {
				permissions.add(new PermissionAttachmentInfo(this, permission, null, false));
			}
		}

		return permissions;
	}

	@Override
	public boolean hasPermission(@Nonnull String permission) {
		if (getPermissionPlayer() == null) return false;
		return permissionPlayer.hasPermissionHere(permission);
	}

	@Override
	public boolean hasPermission(@Nonnull Permission permission) {
		return hasPermission(permission.getName());
	}

	@Override
	public boolean isPermissionSet(@Nonnull String permission) {
		return hasPermission(permission);
	}

	@Override
	public boolean isPermissionSet(@Nonnull Permission permission) {
		return hasPermission(permission);
	}

	@Nullable
	public PermissionPlayer getPermissionPlayer() {
		if (permissionPlayer == null)
			permissionPlayer = CloudDriver.getInstance().getPermissionManager().getPlayerByUniqueId(player.getUniqueId());

		return permissionPlayer;
	}
}
