package net.anweisen.cloud.driver.service.config;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.NetworkingApiUser;
import net.anweisen.cloud.driver.network.packet.def.TemplateStoragePacket;
import net.anweisen.cloud.driver.network.packet.def.TemplateStoragePacket.TemplateStoragePacketType;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteTemplateStorage implements TemplateStorage, NetworkingApiUser {

	private final String name;

	public RemoteTemplateStorage(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nonnull
	@Override
	public Collection<ServiceTemplate> getTemplates() {
		return getTemplatesAsync().getBeforeTimeout(5, TimeUnit.SECONDS);
	}

	@Nullable
	@Override
	public InputStream zipTemplate(@Nonnull ServiceTemplate template) {
		return zipTemplateAsync(template).getOrDefault(90, TimeUnit.SECONDS, null);
	}

	@Nonnull
	@Override
	public Task<Collection<ServiceTemplate>> getTemplatesAsync() {
		return sendPacketQueryAsync(new TemplateStoragePacket(TemplateStoragePacketType.GET_TEMPLATES, buffer -> buffer.writeString(name)))
			.map(packet -> packet.getBuffer().readObjectCollection(ServiceTemplate.class));
	}

	@Override
	@Nonnull
	public Task<InputStream> zipTemplateAsync(@Nonnull ServiceTemplate template) {
		Preconditions.checkNotNull(template);
		Preconditions.checkArgument(template.getStorage().equals(name), "The given ServiceTemplate must be from this TemplateStorage");

		return sendChunkedPacketQuery(
			new TemplateStoragePacket(TemplateStoragePacketType.LOAD_TEMPLATE_STREAM, buffer -> buffer.writeObject(template))
		).map(response -> {
			if (!response.getSession().getHeader().getBoolean("exists")) return null;
			return response.getInputStream();
		});
	}

	@Override
	public boolean hasTemplate(@Nonnull ServiceTemplate template) {
		return hasTemplateAsync(template).getBeforeTimeout(5, TimeUnit.SECONDS);
	}

	@Nonnull
	@Override
	public Task<Boolean> hasTemplateAsync(@Nonnull ServiceTemplate template) {
		return sendPacketQueryAsync(new TemplateStoragePacket(TemplateStoragePacketType.HAS_TEMPLATE, buffer -> buffer.writeObject(template)))
			.map(packet -> packet.getBuffer().readBoolean());
	}

	@Override
	public String toString() {
		return "RemoteTemplateStorage[" + name + "]";
	}
}
