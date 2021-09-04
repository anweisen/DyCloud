package net.anweisen.cloud.modules.perms.bukkit;

import net.anweisen.cloud.modules.perms.bukkit.listener.BukkitCloudPermsListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BukkitCloudPermsPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		initListeners();
		initInjections();
	}

	private void initInjections() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			BukkitCloudPermsHelper.injectPermissible(player);
		}
	}

	private void initListeners() {
		Bukkit.getPluginManager().registerEvents(new BukkitCloudPermsListener(), this);
	}

}
