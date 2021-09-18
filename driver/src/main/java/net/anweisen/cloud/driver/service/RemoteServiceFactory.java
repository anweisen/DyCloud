package net.anweisen.cloud.driver.service;

import net.anweisen.cloud.driver.network.NetworkingApiUser;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlType;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteServiceFactory implements ServiceFactory, NetworkingApiUser {

	@Nullable
	@Override
	public ServiceInfo createService(@Nonnull ServiceTask task) {
		return createServiceAsync(task).getBeforeTimeout(60, TimeUnit.SECONDS);
	}

	@Nonnull
	@Override
	public Task<ServiceInfo> createServiceAsync(@Nonnull ServiceTask task) {
		return sendPacketQueryAsync(new ServiceControlPacket(ServiceControlType.CREATE, task), buffer -> buffer.readOptionalObject(ServiceInfo.class));
	}
}
