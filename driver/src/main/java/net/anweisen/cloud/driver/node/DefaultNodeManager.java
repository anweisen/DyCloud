package net.anweisen.cloud.driver.node;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.event.node.NodeConnectedEvent;
import net.anweisen.cloud.driver.event.node.NodeDisconnectedEvent;
import net.anweisen.cloud.driver.network.packet.def.NodePublishPacket.NodePublishPayload;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultNodeManager implements NodeManager, LoggingApiUser {

	@Override
	public void handleNodeUpdate(@Nonnull NodePublishPayload payload, @Nonnull NodeInfo info) {
		debug("{} -> {}", payload, info);

		switch (payload) {
			case CONNECTED:
				CloudDriver.getInstance().getEventManager().callEvent(new NodeConnectedEvent(info));
				break;
			case DISCONNECTED:
				CloudDriver.getInstance().getEventManager().callEvent(new NodeDisconnectedEvent(info));
				break;
		}
	}

}
