package net.anweisen.cloud.driver.service.specific;

import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlType;
import net.anweisen.cloud.driver.network.request.NetworkingApiUser;
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
		return sendQueryAsync(new ServiceControlPacket(ServiceControlType.START, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> stopAsync() {
		return sendQueryAsync(new ServiceControlPacket(ServiceControlType.STOP, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> restartAsync() {
		return sendQueryAsync(new ServiceControlPacket(ServiceControlType.RESTART, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> killAsync() {
		return sendQueryAsync(new ServiceControlPacket(ServiceControlType.KILL, service.getUniqueId()), buffer -> null);
	}

	@Nonnull
	@Override
	public Task<Void> deleteAsync() {
		return sendQueryAsync(new ServiceControlPacket(ServiceControlType.DELETE, service.getUniqueId()), buffer -> null);
	}
}
