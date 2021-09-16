package net.anweisen.cloud.modules.perms.velocity;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.Player;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class VelocityCloudPermsProvider implements PermissionProvider {

	public static final VelocityCloudPermsProvider INSTANCE = new VelocityCloudPermsProvider();

	private VelocityCloudPermsProvider() {
	}

	@Override
	public PermissionFunction createFunction(PermissionSubject subject) {
		return subject instanceof Player ? new VelocityCloudPermsFunction((Player) subject) : null;
	}
}
