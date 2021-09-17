package net.anweisen.cloud.modules.chattab.bukkit;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.modules.chattab.bukkit.handler.DefaultChatHandler;
import net.anweisen.cloud.modules.chattab.bukkit.handler.DefaultTabHandler;
import net.anweisen.cloud.modules.chattab.bukkit.listener.BukkitChatListener;
import net.anweisen.cloud.modules.chattab.bukkit.listener.BukkitTabListener;
import net.anweisen.cloud.modules.chattab.config.ChatTabConfig;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class BukkitCloudChatTabPlugin extends JavaPlugin {

	private static BukkitCloudChatTabPlugin instance;

	private BukkitCloudChatTabManager manager;
	private ChatTabConfig config;

	@Override
	public void onLoad() {
		instance = this;

		manager = new BukkitCloudChatTabManager();

		updateConfig();
	}

	public void updateConfig() {
		this.config = CloudDriver.getInstance().getGlobalConfig().get("chattabConfig", ChatTabConfig.class);

		if (config.getTablist().isEnabled() && manager.getTabHandler() == null)
			manager.setTabHandler(new DefaultTabHandler());
		if (config.getChat().isEnabled() && manager.getChatHandler() == null)
			manager.setChatHandler(new DefaultChatHandler());

		if (manager.getTabHandler() != null)
			manager.getTabHandler().update();
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BukkitChatListener(), this);
		getServer().getPluginManager().registerEvents(new BukkitTabListener(), this);
	}

	@Nonnull
	public BukkitCloudChatTabManager getManager() {
		return manager;
	}

	@Nonnull
	public ChatTabConfig getChatTabConfig() {
		return config;
	}

	public static BukkitCloudChatTabPlugin getInstance() {
		return instance;
	}
}
