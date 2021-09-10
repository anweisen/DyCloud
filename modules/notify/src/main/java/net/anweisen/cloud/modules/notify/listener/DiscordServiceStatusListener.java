package net.anweisen.cloud.modules.notify.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.*;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.notify.CloudNotifyModule;
import net.anweisen.utilities.common.discord.DiscordWebhook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DiscordServiceStatusListener {

	@EventListener
	public void onServiceRegistered(@Nonnull ServiceRegisteredEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getDiscord().getCreatingMessage());
	}

	@EventListener
	public void onServiceStarted(@Nonnull ServiceStartedEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getDiscord().getStartingMessage());
	}

	@EventListener
	public void onServiceReady(@Nonnull ServiceReadyEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getDiscord().getStartedMessage());
	}

	@EventListener
	public void onServiceStopped(@Nonnull ServiceStoppedEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getDiscord().getStoppedMessage());
	}

	@EventListener
	public void onServiceDeleted(@Nonnull ServiceUnregisteredEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getDiscord().getDeletedMessage());
	}

	private void sendMessage(@Nonnull ServiceInfo service, @Nullable DiscordWebhook webhook) {
		if (webhook == null) return;
		CloudDriver.getInstance().getExecutor().submit((Runnable) webhook.clone()
				.replaceEverywhere("{service}", service.getName())
				.replaceEverywhere("{node}", service.getNodeName())
		);
	}

}
