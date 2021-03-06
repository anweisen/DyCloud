package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceControlPacket extends Packet {

	/** @see net.anweisen.cloud.driver.service.RemoteServiceFactory#createServiceAsync(ServiceTask) */
	public ServiceControlPacket(@Nonnull ServiceControlPayload payload, @Nonnull ServiceTask task) {
		super(PacketChannels.SERVICE_CONTROL_CHANNEL, newBuffer().writeEnum(payload).writeObject(task));
	}

	public ServiceControlPacket(@Nonnull ServiceControlPayload payload, @Nonnull ServiceInfo service) {
		super(PacketChannels.SERVICE_CONTROL_CHANNEL, newBuffer().writeEnum(payload).writeObject(service));
	}

	public ServiceControlPacket(@Nonnull ServiceControlPayload payload, @Nonnull UUID service) {
		super(PacketChannels.SERVICE_CONTROL_CHANNEL, newBuffer().writeEnum(payload).writeUniqueId(service));
	}

	public enum ServiceControlPayload {
		CREATE,
		START,
		STOP,
		KILL,
		RESTART,
		DELETE
	}

}
