package net.anweisen.cloud.modules.perms;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.driver.player.permission.impl.CloudPermissionManager;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudPermsModule extends CloudModule {

	@Override
	protected void onLoad() {
		if (getEnabled(true)) {
			getDriver().setPermissionManager(new CloudPermissionManager());
		}
	}

}
