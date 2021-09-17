package net.anweisen.cloud.modules.bridge.bukkit;

import net.anweisen.cloud.modules.bridge.bukkit.listener.BukkitCloudListener;
import net.anweisen.cloud.modules.bridge.bukkit.listener.BukkitPlayerListener;
import net.anweisen.cloud.modules.bridge.helper.BridgeHelper;
import net.anweisen.cloud.wrapper.CloudWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BukkitCloudBridgePlugin extends JavaPlugin {

	private static BukkitCloudBridgePlugin instance;

	@Override
	public void onEnable() {
		instance = this;

		initListeners();
		initHelpers();
	}

	@Override
	public void onDisable() {
		CloudWrapper.getInstance().getEventManager().unregisterListener(this.getClass().getClassLoader());
	}

	private void initHelpers() {
		BridgeHelper.setMaxPlayers(Bukkit.getMaxPlayers());
		BridgeHelper.setMotd(Bukkit.getMotd());
		BridgeHelper.setStatus("LOBBY");
		BridgeHelper.updateServiceInfo();
	}

	private void initListeners() {
		CloudWrapper.getInstance().getEventManager().registerListeners(new BukkitCloudListener());
		Bukkit.getServer().getPluginManager().registerEvents(new BukkitPlayerListener(), this);
	}

	public static BukkitCloudBridgePlugin getInstance() {
		return instance;
	}
}
