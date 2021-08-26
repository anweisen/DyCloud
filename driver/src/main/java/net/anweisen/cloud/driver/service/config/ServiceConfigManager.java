package net.anweisen.cloud.driver.service.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.service.specific.ServiceEnvironment;
import net.anweisen.cloud.driver.service.specific.ServiceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getServiceConfigManager()
 */
public interface ServiceConfigManager {

	@Nonnull
	Collection<ServiceTask> getTasks();

	@Nonnull
	default Collection<ServiceTask> getTasks(@Nonnull ServiceType type) {
		return getTasks().stream().filter(task -> task.getEnvironment().getServiceType() == type).collect(Collectors.toList());
	}

	@Nonnull
	default Collection<ServiceTask> getTasks(@Nonnull ServiceEnvironment environment) {
		return getTasks().stream().filter(task -> task.getEnvironment() == environment).collect(Collectors.toList());
	}

	@Nullable
	ServiceTask getTask(@Nonnull String name);

	void registerServiceTask(@Nonnull ServiceTask task);

	void setServiceTasks(@Nonnull Collection<? extends ServiceTask> tasks);

	@Nonnull
	Collection<TemplateStorage> getTemplateStorages();

	@Nonnull
	Collection<String> getTemplateStorageNames();

	@Nullable
	TemplateStorage getTemplateStorage(@Nonnull String name);

	void registerTemplateStorage(@Nonnull TemplateStorage storage);

	void setTemplateStorages(@Nonnull Collection<? extends TemplateStorage> storages);

}
