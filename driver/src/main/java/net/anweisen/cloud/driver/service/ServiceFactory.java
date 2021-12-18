package net.anweisen.cloud.driver.service;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utility.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getServiceFactory()
 */
public interface ServiceFactory {

	@Nullable
	ServiceInfo createService(@Nonnull ServiceTask task);

	@Nonnull
	Task<ServiceInfo> createServiceAsync(@Nonnull ServiceTask task);

}
