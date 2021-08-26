package net.anweisen.cloud.modules.perms;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.player.permission.impl.CloudPermissionManager;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudPermsModule extends CloudModule {

	@Override
	protected void onLoad() {
		if (!getConfig().contains("enabled")) {
			getConfig().set("enabled", true).save();
		}
		getLogger().debug("CloudPermsModule Status: enabled={}", getConfig().getBoolean("enabled"));

		if (getDriver().getEnvironment() != DriverEnvironment.MASTER) return;
		if (getConfig().getBoolean("enabled")) {
			getDriver().setPermissionManager(new CloudPermissionManager());
		}
	}

}
