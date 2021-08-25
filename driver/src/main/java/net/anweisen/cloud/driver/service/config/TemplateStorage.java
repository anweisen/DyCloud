package net.anweisen.cloud.driver.service.config;

import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Multiple template storages can be used to distribute templates over several folders or machines.
 * They may be local or remote.
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see LocalTemplateStorage
 * @see RemoteTemplateStorage
 *
 * @see ServiceConfigManager
 */
public interface TemplateStorage {

	@Nonnull
	String getName();

	@Nullable
	InputStream zipTemplate(@Nonnull ServiceTemplate template) throws IOException;

	@Nonnull
	Task<InputStream> zipTemplateAsync(@Nonnull ServiceTemplate template);

	@Nonnull
	Collection<ServiceTemplate> getTemplates();

	@Nonnull
	Task<Collection<ServiceTemplate>> getTemplatesAsync();

	boolean hasTemplate(@Nonnull ServiceTemplate template);

	@Nonnull
	Task<Boolean> hasTemplateAsync(@Nonnull ServiceTemplate template);

}
