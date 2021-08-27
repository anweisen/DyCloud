package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.config.ServiceTask;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceControlPacket extends Packet {

	/** @see net.anweisen.cloud.driver.service.RemoteServiceFactory#createServiceAsync(ServiceTask) */
	public ServiceControlPacket(@Nonnull ServiceControlType type, @Nonnull ServiceTask task) {
		super(PacketConstants.SERVICE_CONTROL_CHANNEL, Buffer.create().writeEnumConstant(type).writeObject(task));
	}

	public ServiceControlPacket(@Nonnull ServiceControlType type, @Nonnull UUID service) {
		super(PacketConstants.SERVICE_CONTROL_CHANNEL, Buffer.create().writeEnumConstant(type).writeUUID(service));
	}

	public enum ServiceControlType {
		CREATE,
		START,
		STOP,
		KILL,
		RESTART,
		DELETE
	}

}
