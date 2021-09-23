package net.anweisen.cloud.node.network.listener;

import com.github.dockerjava.api.exception.DockerException;
import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.event.service.ServiceRegisteredEvent;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlType;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
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
		Buffer buffer = packet.getBuffer();

		ServiceControlType type = buffer.readEnumConstant(ServiceControlType.class);

 		if (type == ServiceControlType.CREATE) {
		    ServiceInfo service = buffer.readObject(ServiceInfo.class);
			cloud.getServiceManager().registerService(service);
			cloud.getEventManager().callEvent(new ServiceRegisteredEvent(service));

		    debug("{} -> {}", type, service);
		    ServiceTask task = service.findTask();
		    Preconditions.checkNotNull(task, "ServiceTask of service for action " + type + " is null (" + service.getTaskName() + ")");

		    actor.createServiceHere(service, task);
			channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeObject(service)));
			return;
	    }

		UUID uuid = buffer.readUUID();
		ServiceInfo service = cloud.getServiceManager().getServiceInfoByUniqueId(uuid);
		debug("{} -> {}", type, service);
		Preconditions.checkNotNull(service, "Service for action " + type + " is null (" + uuid + ")");

		if (type == ServiceControlType.START || type == ServiceControlType.RESTART) {
			if (service.getDockerContainerId() == null) {
				warn("Docker container id of service " + service.getName() + " for action is null");
				service.setControlState(ServiceControlState.NONE);
				cloud.publishUpdate(ServicePublishType.UPDATE, service);
				channel.sendPacket(Packet.createResponseFor(packet));
				return;
			}
		}

		if (service.getDockerContainerId() != null) {
			try {
				doServiceAction(service, type, actor);
				debug("Successfully done {} action on {}", type, service);
			} catch (DockerException ex) {
				error("Unable to do service action {} on {}", type, service, ex);
			}
		}

		service.setControlState(ServiceControlState.NONE);
		service.setState(getServiceState(type));
		cloud.publishUpdate(getPublishType(type), service);
		channel.sendPacket(Packet.createResponseFor(packet));
	}

	private void doServiceAction(@Nonnull ServiceInfo service, @Nonnull ServiceControlType type, @Nonnull DockerServiceActor actor) {
		switch (type) {
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
	private ServiceState getServiceState(@Nonnull ServiceControlType type) {
		switch (type) {
			case RESTART:
			case STOP:
			case KILL:      return ServiceState.STOPPED;
			case DELETE:    return ServiceState.DELETED;
			case START:     return ServiceState.RUNNING;
			default:        throw new IllegalArgumentException(type.name());
		}
	}

	private ServicePublishType getPublishType(@Nonnull ServiceControlType type) {
		switch (type) {
			case START:     return ServicePublishType.STARTED;
			case RESTART:   return ServicePublishType.RESTARTED;
			case KILL:      return ServicePublishType.KILLED;
			case DELETE:    return ServicePublishType.UNREGISTER;
			case STOP:      return ServicePublishType.STOPPED;
			default:        throw new IllegalArgumentException(type.name());
		}
	}

}
