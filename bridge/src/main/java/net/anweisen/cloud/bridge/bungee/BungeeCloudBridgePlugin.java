package net.anweisen.cloud.bridge.bungee;

import net.anweisen.cloud.bridge.bungee.listener.BungeeCloudListener;
import net.anweisen.cloud.bridge.general.BridgeCloudListener;
import net.anweisen.cloud.bridge.helper.BridgeHelper;
import net.anweisen.cloud.driver.CloudDriver;
import net.md_5.bungee.api.plugin.Plugin;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudBridgePlugin extends Plugin {

	private static BungeeCloudBridgePlugin instance;

	@Override
	public void onLoad() {
		instance = this;

		initHelpers();
		initListeners();
	}

	private void initHelpers() {
		BridgeHelper.setMaxPlayers(this.getProxy().getConfig().getPlayerLimit());
		BridgeHelper.updateServiceInfo();
	}

	private void initListeners() {
		CloudDriver.getInstance().getEventManager().registerListeners(new BridgeCloudListener(), new BungeeCloudListener());
	}

	@Nonnull
	public static BungeeCloudBridgePlugin getInstance() {
		return instance;
	}
}
