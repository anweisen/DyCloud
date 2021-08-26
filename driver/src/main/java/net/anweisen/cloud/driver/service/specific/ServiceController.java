package net.anweisen.cloud.driver.service.specific;

import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface ServiceController {

	@Nonnull
	ServiceInfo getService();

	void start();

	@Nonnull
	Task<Void> startAsync();

	void stop();

	@Nonnull
	Task<Void> stopAsync();

	void restart();

	@Nonnull
	Task<Void> restartAsync();

	void kill();

	@Nonnull
	Task<Void> killAsync();

	void delete();

	@Nonnull
	Task<Void> deleteAsync();

}
