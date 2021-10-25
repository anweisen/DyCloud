package net.anweisen.cloud.driver.service.config;

import net.anweisen.cloud.driver.network.NetworkingApiUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteServiceConfigManager implements ServiceConfigManager, NetworkingApiUser {

	private final Map<String, ServiceTask> tasks = new LinkedHashMap<>();
	private final Map<String, TemplateStorage> storages = new LinkedHashMap<>();

	@Nonnull
	@Override
	public Collection<ServiceTask> getServiceTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	@Nonnull
	@Override
	public Collection<String> getServiceTaskNames() {
		List<String> names = new ArrayList<>(tasks.size());
		tasks.forEach((key, task) -> names.add(task.getName()));
		return names;
	}

	@Nullable
	@Override
	public ServiceTask getServiceTask(@Nonnull String name) {
		return tasks.get(name);
	}

	@Override
	public void registerServiceTask(@Nonnull ServiceTask task) {
		tasks.put(task.getName(), task);
	}

	@Override
	public void setServiceTaskCache(@Nonnull Collection<? extends ServiceTask> tasks) {
		this.tasks.clear();
		for (ServiceTask task : tasks)
			registerServiceTask(task);
	}

	@Nonnull
	@Override
	public Collection<TemplateStorage> getTemplateStorages() {
		return Collections.unmodifiableCollection(storages.values());
	}

	@Nonnull
	@Override
	public Collection<String> getTemplateStorageNames() {
		return storages.keySet();
	}

	@Nullable
	@Override
	public TemplateStorage getTemplateStorage(@Nonnull String name) {
		return storages.get(name);
	}

	@Override
	public void registerTemplateStorage(@Nonnull TemplateStorage storage) {
		storages.put(storage.getName(), storage);
	}

	@Override
	public void setTemplateStorages(@Nonnull Collection<? extends TemplateStorage> storages) {
		this.storages.clear();
		for (TemplateStorage storage : storages)
			registerTemplateStorage(storage);
	}
}
