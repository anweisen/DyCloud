package net.anweisen.cloud.driver.service.specific;

import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see ServiceInfo#getController()
 * @see ServiceManager#getController(ServiceInfo)
 */
public interface ServiceController {

	@Nonnull
	ServiceInfo getService();

	default void start() {
		startAsync().getBeforeTimeout(30, TimeUnit.SECONDS);
	}

	@Nonnull
	Task<Void> startAsync();

	default void stop() {
		stopAsync().getBeforeTimeout(30, TimeUnit.SECONDS);
	}

	@Nonnull
	Task<Void> stopAsync();

	default void restart() {
		restartAsync().getBeforeTimeout(30, TimeUnit.SECONDS);
	}

	@Nonnull
	Task<Void> restartAsync();

	default void kill() {
		killAsync().getBeforeTimeout(30, TimeUnit.SECONDS);
	}

	@Nonnull
	Task<Void> killAsync();

	default void delete() {
		deleteAsync().getBeforeTimeout(30, TimeUnit.SECONDS);
	}

	@Nonnull
	Task<Void> deleteAsync();

}
