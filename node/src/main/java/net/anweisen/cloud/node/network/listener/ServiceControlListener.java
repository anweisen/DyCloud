package net.anweisen.cloud.node.network.listener;

import com.github.dockerjava.api.exception.DockerException;
import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.event.service.ServiceRegisteredEvent;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlPayload;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceControlState;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceState;
import net.anweisen.cloud.node.CloudNode;
import net.anweisen.cloud.node.docker.DockerServiceActor;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceControlListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudNode cloud = CloudNode.getInstance();
		DockerServiceActor actor = cloud.getServiceActor();
		PacketBuffer buffer = packet.getBuffer();

		ServiceControlPayload payload = buffer.readEnum(ServiceControlPayload.class);

 		if (payload == ServiceControlPayload.CREATE) {
		    ServiceInfo service = buffer.readObject(ServiceInfo.class);
			cloud.getServiceManager().registerService(service);
			cloud.getEventManager().callEvent(new ServiceRegisteredEvent(service));

		    debug("{} -> {}", payload, service);
		    ServiceTask task = service.findTask();
		    Preconditions.checkNotNull(task, "ServiceTask of service for action " + payload + " is null (" + service.getTaskName() + ")");

		    actor.createServiceHere(service, task);
			channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeObject(service)));
			return;
	    }

		UUID uuid = buffer.readUniqueId();
		ServiceInfo service = cloud.getServiceManager().getServiceInfoByUniqueId(uuid);
		debug("{} -> {}", payload, service);
		Preconditions.checkNotNull(service, "Service for action " + payload + " is null (" + uuid + ")");

		if (payload == ServiceControlPayload.START || payload == ServiceControlPayload.RESTART) {
			if (service.getDockerContainerId() == null) {
				warn("Docker container id of service " + service.getName() + " for action is null");
				service.setControlState(ServiceControlState.NONE);
				cloud.publishUpdate(ServicePublishPayload.UPDATE, service);
				channel.sendPacket(Packet.createResponseFor(packet));
				return;
			}
		}

		if (service.getDockerContainerId() != null) {
			try {
				doServiceAction(service, payload, actor);
				debug("Successfully done {} action on {}", payload, service);
			} catch (DockerException ex) {
				error("Unable to do service action {} on {}", payload, service, ex);
			}
		}

		service.setControlState(ServiceControlState.NONE);
		service.setState(getServiceState(payload));
		cloud.publishUpdate(getPublishPayload(payload), service);
		channel.sendPacket(Packet.createResponseFor(packet));
	}

	private void doServiceAction(@Nonnull ServiceInfo service, @Nonnull ServiceControlPayload payload, @Nonnull DockerServiceActor actor) {
		switch (payload) {
			case STOP:
				actor.stopService(service);
				break;
			case DELETE:
				actor.deleteService(service);
				break;
			case KILL:
				actor.killService(service);
				break;
			case RESTART:
				actor.restartService(service);
				break;
			case START:
				actor.startService(service);
				break;
		}
	}

	@Nonnull
	private ServiceState getServiceState(@Nonnull ServiceControlPayload payload) {
		switch (payload) {
			case RESTART:
			case STOP:
			case KILL:      return ServiceState.STOPPED;
			case DELETE:    return ServiceState.DELETED;
			case START:     return ServiceState.RUNNING;
			default:        throw new IllegalArgumentException(payload.name());
		}
	}

	private ServicePublishPayload getPublishPayload(@Nonnull ServiceControlPayload payload) {
		switch (payload) {
			case START:     return ServicePublishPayload.STARTED;
			case RESTART:   return ServicePublishPayload.RESTARTED;
			case KILL:      return ServicePublishPayload.KILLED;
			case DELETE:    return ServicePublishPayload.UNREGISTER;
			case STOP:      return ServicePublishPayload.STOPPED;
			default:        throw new IllegalArgumentException(payload.name());
		}
	}

}
