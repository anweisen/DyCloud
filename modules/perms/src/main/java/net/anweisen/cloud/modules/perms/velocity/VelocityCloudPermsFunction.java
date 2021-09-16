package net.anweisen.cloud.modules.perms.velocity;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class VelocityCloudPermsFunction implements PermissionFunction {

	private final Player player;

	private PermissionPlayer permissionPlayer;

	public VelocityCloudPermsFunction(@Nonnull Player player) {
		this.player = player;
	}

	@Override
	public Tristate getPermissionValue(@Nonnull String permission) {
		if (getPermissionPlayer() == null) return Tristate.UNDEFINED;
		return Tristate.fromBoolean(permissionPlayer.hasPermissionHere(permission));
	}

	@Nullable
	public PermissionPlayer getPermissionPlayer() {
		if (permissionPlayer == null)
			permissionPlayer = CloudDriver.getInstance().getPermissionManager().getPlayerByUniqueId(player.getUniqueId());

		return permissionPlayer;
	}
}
