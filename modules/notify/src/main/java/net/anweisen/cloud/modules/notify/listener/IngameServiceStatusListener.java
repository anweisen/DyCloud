package net.anweisen.cloud.modules.notify.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.*;
import net.anweisen.cloud.driver.player.chat.ChatClickReaction;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.player.permission.Permissions;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.notify.CloudNotifyModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class IngameServiceStatusListener {

	@EventListener
	public void onServiceRegistered(@Nonnull ServiceRegisteredEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getIngame().getCreatingMessage(), false);
	}

	@EventListener
	public void onServiceStarted(@Nonnull ServiceStartedEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getIngame().getStartingMessage(), true);
	}

	@EventListener
	public void onServiceReady(@Nonnull ServiceReadyEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getIngame().getStartedMessage(), true);
	}

	@EventListener
	public void onServiceStopped(@Nonnull ServiceStoppedEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getIngame().getStoppedMessage(), false);
	}

	@EventListener
	public void onServiceDeleted(@Nonnull ServiceUnregisteredEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getIngame().getDeletedMessage(), false);
	}

	private void sendMessage(@Nonnull ServiceInfo service, @Nullable String message, boolean connect) {
		if (message == null) return;

		ChatText text = new ChatText(message
			.replace("{service}", service.getName())
			.replace("{node}", service.getNodeName())
		);

		if (connect) {
			text.setClick(ChatClickReaction.RUN_COMMAND, "/server " + service.getName());
			if (CloudNotifyModule.getInstance().getNotifyConfig().getIngame().getHoverConnectMessage() != null)
				text.setHover(CloudNotifyModule.getInstance().getNotifyConfig().getIngame().getHoverConnectMessage());
		}

		CloudDriver.getInstance().getPlayerManager().getGlobalExecutor().sendMessage(Permissions.NOTIFY, text);
	}

}
