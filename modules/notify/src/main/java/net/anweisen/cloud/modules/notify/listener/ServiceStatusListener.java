package net.anweisen.cloud.modules.notify.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.driver.event.service.ServiceRegisteredEvent;
import net.anweisen.cloud.driver.event.service.ServiceStartedEvent;
import net.anweisen.cloud.driver.event.service.ServiceStoppedEvent;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.player.chat.ChatClickEvent;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.modules.notify.CloudNotifyModule;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceStatusListener {

	private static final String permission = "cloud.notify";

	@EventListener
	public void onServiceRegistered(@Nonnull ServiceRegisteredEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getStartingMessage());
	}

	@EventListener
	public void onServiceStarted(@Nonnull ServiceStartedEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getStartedMessage());
	}

	@EventListener
	public void onServiceStopped(@Nonnull ServiceStoppedEvent event) {
		sendMessage(event.getServiceInfo(), CloudNotifyModule.getInstance().getNotifyConfig().getStoppedMessage());
	}

	private void sendMessage(@Nonnull ServiceInfo service, @Nonnull String message) {
		ChatText text = new ChatText(message
			.replace("{service}", service.getName())
			.replace("{node}", service.getNodeName())
		).addClick(ChatClickEvent.RUN_COMMAND, "/server " + service.getName())
		.addHover(CloudNotifyModule.getInstance().getNotifyConfig().getHoverMessage());

		for (CloudPlayer player : CloudDriver.getInstance().getPlayerManager().getOnlinePlayers()) {
			player.getExecutor().sendMessage(permission, text);
		}

	}

}
