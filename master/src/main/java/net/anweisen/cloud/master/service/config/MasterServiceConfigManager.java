package net.anweisen.cloud.master.service.config;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.config.TemplateStorage;
import net.anweisen.utility.common.misc.FileUtils;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterServiceConfigManager implements ServiceConfigManager, LoggingApiUser {

	private static final Path directory = Paths.get("tasks");

	private final Map<String, ServiceTask> tasks = new LinkedHashMap<>();
	private final Map<String, TemplateStorage> templateStorages = new LinkedHashMap<>();

	public MasterServiceConfigManager() {
		FileUtils.createDirectory(directory);
	}

	public void loadTasks() throws IOException {
		tasks.clear();
		for (Path file : FileUtils.list(directory).filter(path -> path.toString().endsWith(".json")).collect(Collectors.toList())) {
			extended("Loading task '{}'", file.getFileName());
			Document document = Documents.newJsonDocument(file);
			ServiceTask task = document.toInstance(ServiceTask.class);
			extended("=> {}", task);
			if (task != null) tasks.put(task.getName(), task);
		}
	}

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
		return templateStorages.values();
	}

	@Nonnull
	@Override
	public Collection<String> getTemplateStorageNames() {
		return templateStorages.keySet();
	}

	@Nullable
	@Override
	public TemplateStorage getTemplateStorage(@Nonnull String name) {
		return templateStorages.get(name);
	}

	@Override
	public void registerTemplateStorage(@Nonnull TemplateStorage storage) {
		templateStorages.put(storage.getName(), storage);
	}

	@Override
	public void setTemplateStorages(@Nonnull Collection<? extends TemplateStorage> storages) {
		templateStorages.clear();
		for (TemplateStorage storage : storages)
			registerTemplateStorage(storage);
	}
}
