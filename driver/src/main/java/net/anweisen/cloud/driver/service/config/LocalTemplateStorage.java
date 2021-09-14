package net.anweisen.cloud.driver.service.config;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class LocalTemplateStorage implements TemplateStorage, LoggingApiUser {

	@Nonnull
	public static LocalTemplateStorage createDefault() {
		return new LocalTemplateStorage("default", Paths.get("templates"));
	}

	private final String name;
	private final Path storageDirectory;

	public LocalTemplateStorage(@Nonnull String name, @Nonnull Path storageDirectory) {
		this.name = name;
		this.storageDirectory = storageDirectory;

		FileUtils.createDirectory(storageDirectory);
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nonnull
	@Override
	public Collection<ServiceTemplate> getTemplates() {
		try {
			return Files.list(this.storageDirectory)
				.filter(Files::isDirectory)
				.map(path -> new ServiceTemplate(name, storageDirectory.relativize(path).getName(0).toString()))
				.collect(Collectors.toList());
		} catch (IOException ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@Override
	public Task<Collection<ServiceTemplate>> getTemplatesAsync() {
		return Task.completed(getTemplates());
	}

	@Nullable
	@Override
	public InputStream zipTemplate(@Nonnull ServiceTemplate template) throws IOException {
		if (!hasTemplate(template)) return null;

		Path directory = this.storageDirectory.resolve(template.getName());
		Path tempFile = FileUtils.createTempFile();

		trace("Ziping template {} to temp file..", template.toShortString());
		Path file = FileUtils.zipToFile(directory, tempFile);
		trace("Finished ziping template {}", template.toShortString());
		if (file == null) return null;

		return Files.newInputStream(file, StandardOpenOption.DELETE_ON_CLOSE, LinkOption.NOFOLLOW_LINKS);
	}

	@Nonnull
	@Override
	public Task<InputStream> zipTemplateAsync(@Nonnull ServiceTemplate template) {
		return Task.asyncCall(() -> zipTemplate(template));
	}

	@Nonnull
	@Override
	public Task<Boolean> hasTemplateAsync(@Nonnull ServiceTemplate template) {
		return Task.completed(hasTemplate(template));
	}

	@Override
	public boolean hasTemplate(@Nonnull ServiceTemplate template) {
		Preconditions.checkNotNull(template);
		Preconditions.checkArgument(template.getStorage().equals(name), "The given ServiceTemplate must be from this TemplateStorage");

		return Files.exists(this.storageDirectory.resolve(template.getName()));
	}

	@Nonnull
	public Path getStorageDirectory() {
		return storageDirectory;
	}

	@Override
	public String toString() {
		return "LocalTemplateStorage[" + name + "]";
	}
}
