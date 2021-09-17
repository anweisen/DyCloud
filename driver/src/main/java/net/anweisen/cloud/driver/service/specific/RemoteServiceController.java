package net.anweisen.cloud.driver.service.specific;

import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlType;
import net.anweisen.cloud.driver.network.NetworkingApiUser;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteServiceController implements ServiceController, NetworkingApiUser {

	private final ServiceInfo service;

	public RemoteServiceController(@Nonnull ServiceInfo service) {
		this.service = service;
	}

	@Nonnull
	@Override
	public ServiceInfo getService() {
		return service;
	}

	@Nonnull
	@Override
	public Task<Void> startAsync() {
		return sendPacketQueryAsync(new ServiceControlPacket(ServiceControlType.START, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> stopAsync() {
		return sendPacketQueryAsync(new ServiceControlPacket(ServiceControlType.STOP, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> restartAsync() {
		return sendPacketQueryAsync(new ServiceControlPacket(ServiceControlType.RESTART, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> killAsync() {
		return sendPacketQueryAsync(new ServiceControlPacket(ServiceControlType.KILL, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> deleteAsync() {
		return sendPacketQueryAsync(new ServiceControlPacket(ServiceControlType.DELETE, service.getUniqueId()), buffer -> null);
	}
}
