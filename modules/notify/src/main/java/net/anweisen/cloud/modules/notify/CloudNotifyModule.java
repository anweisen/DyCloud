package net.anweisen.cloud.modules.notify;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.modules.notify.config.NotifyConfig;
import net.anweisen.cloud.modules.notify.config.NotifyDiscordConfig;
import net.anweisen.cloud.modules.notify.config.NotifyIngameConfig;
import net.anweisen.cloud.modules.notify.listener.DiscordServiceStatusListener;
import net.anweisen.cloud.modules.notify.listener.IngameServiceStatusListener;
import net.anweisen.utilities.common.collection.Colors;
import net.anweisen.utilities.common.discord.DiscordWebhook;
import net.anweisen.utilities.common.discord.DiscordWebhook.EmbedObject;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudNotifyModule extends CloudModule {

	private static CloudNotifyModule instance;

	private NotifyConfig config;

	@Override
	protected void onLoad() {
		instance = this;

		loadConfig();

		if (config.getIngame().isEnabled())
			registerListeners(new IngameServiceStatusListener());
		if (config.getDiscord().isEnabled())
			registerListeners(new DiscordServiceStatusListener());
	}

	private void loadConfig() {
		config = getConfig().toInstanceOf(NotifyConfig.class);
		getLogger().debug("Loaded config {}", config);
		if (config == null)
			getConfig().set(config = new NotifyConfig(
				new NotifyIngameConfig(
					true,
					"§e{service} §7is being §e§lcreated §7on §e{node}§7..",
					"§e{service} §7is being §a§lstarted §7on §e{node}§7..",
					"§e{service} §7was §2§lstarted §7on §e{node}§7..",
					"§e{service} §7was §c§lstopped §7on §e{node}§7..",
					"§e{service} §7was §4§ldeleted §7on §e{node}§7..",
					"§8● §7Click to §2§lconnect"
				),
				new NotifyDiscordConfig(
					false,
					new DiscordWebhook("").setUsername("Cloud").addEmbed(new EmbedObject().setDescription("**{service}** is being **created** on **{node}**..").setColor(Colors.OFFLINE)),
					new DiscordWebhook("").setUsername("Cloud").addEmbed(new EmbedObject().setDescription("**{service}** is being **started** on **{node}**..").setColor(Color.decode("#87fdba"))),
					new DiscordWebhook("").setUsername("Cloud").addEmbed(new EmbedObject().setDescription("**{service}** was **started** on **{node}**").setColor(Colors.ONLINE)),
					new DiscordWebhook("").setUsername("Cloud").addEmbed(new EmbedObject().setDescription("**{service}** was **stopped** on **{node}**").setColor(Colors.DO_NOT_DISTURB)),
					new DiscordWebhook("").setUsername("Cloud").addEmbed(new EmbedObject().setDescription("**{service}** was **deleted** on **{node}**").setColor(Color.decode("#c51010")))
				)
			)).save();
	}

	@Override
	public boolean isEnabled() {
		return config.getIngame().isEnabled() || config.getDiscord().isEnabled();
	}

	@Nonnull
	public NotifyConfig getNotifyConfig() {
		return config;
	}

	public static CloudNotifyModule getInstance() {
		return instance;
	}
}
