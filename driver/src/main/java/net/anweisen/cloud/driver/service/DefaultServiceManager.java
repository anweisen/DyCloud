package net.anweisen.cloud.driver.service;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.event.service.*;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishType;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultServiceManager implements ServiceManager, LoggingApiUser {

	@Override
	public void handleServiceUpdate(@Nonnull ServicePublishType type, @Nonnull ServiceInfo info) {
		debug("{} -> {} uuid={}", type, info, info.getUniqueId());

		if (type == ServicePublishType.UNREGISTER) {
			unregisterServiceInfoInternally(info);
		} else {
			updateServiceInfoInternally(info);
		}

		switch (type) {
			case UPDATE:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceUpdateEvent(info));
				break;
			case STARTED:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceStartedEvent(info));
				break;
			case RESTARTED:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceRestartedEvent(info));
				break;
			case STOPPED:
			case KILLED:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceStoppedEvent(info));
				break;
			case REGISTER:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceRegisteredEvent(info));
				break;
			case UNREGISTER:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceUnregisteredEvent(info));
				break;
			case CONNECTED:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceConnectedEvent(info));
				break;
			case DISCONNECTED:
				CloudDriver.getInstance().getEventManager().callEvent(new ServiceDisconnectedEvent(info));
				break;
		}
	}

	protected abstract void updateServiceInfoInternally(@Nonnull ServiceInfo newServiceInfo);

	protected abstract void unregisterServiceInfoInternally(@Nonnull ServiceInfo serviceInfo);

}
