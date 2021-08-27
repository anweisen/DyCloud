package net.anweisen.cloud.node.network.listener;

import com.github.dockerjava.api.exception.DockerException;
import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlType;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.PublishType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.node.CloudNode;
import net.anweisen.cloud.node.service.NodeServiceActor;

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
		Buffer buffer = packet.getBuffer();

		ServiceControlType type = buffer.readEnumConstant(ServiceControlType.class);
		UUID uuid = buffer.readUUID();
		ServiceInfo service = cloud.getServiceManager().getServiceInfoByUUID(uuid);
		debug("{} -> {}", type, service);
		Preconditions.checkNotNull(service, "Service for action is null");
		ServiceTask task = service.findTask();
		Preconditions.checkNotNull(task, "ServiceTask of service is null");
		NodeServiceActor actor = cloud.getServiceActor();

 		if (type == ServiceControlType.CREATE) {
			actor.createServiceHere(service, task);
			channel.sendPacket(Packet.createResponseFor(packet));
			return;
	    }

		Preconditions.checkNotNull(service.getDockerContainerId(), "Docker container id of service for action is null");
		try {
			doServiceAction(service, type, actor);
			cloud.publishUpdate(getPublishType(type), service);
		} catch (DockerException ex) {
			error("Unable to do service action service {}", service, ex);
		}
		channel.sendPacket(Packet.createResponseFor(packet));
	}

	private void doServiceAction(@Nonnull ServiceInfo service, @Nonnull ServiceControlType type, @Nonnull NodeServiceActor actor) {
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

	private PublishType getPublishType(@Nonnull ServiceControlType type) {
		switch (type) {
			case START:     return PublishType.STARTED;
			case RESTART:   return PublishType.RESTARTED;
			case KILL:      return PublishType.KILLED;
			case DELETE:    return PublishType.UNREGISTER;
			case STOP:      return PublishType.STOPPED;
			default:        throw new IllegalArgumentException(type.name());
		}
	}

}
