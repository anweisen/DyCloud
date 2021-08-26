package net.anweisen.cloud.driver.service.config;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.request.NetworkingApiUser;
import net.anweisen.cloud.driver.network.request.RequestResponseType;
import net.anweisen.cloud.driver.network.request.RequestType;
import net.anweisen.utilities.common.collection.WrappedException;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteTemplateStorage implements TemplateStorage, NetworkingApiUser {

	private final String name;
	private final SocketChannel channel;

	public RemoteTemplateStorage(@Nonnull String name, @Nonnull SocketChannel channel) {
		this.name = name;
		this.channel = channel;
	}

	@Nonnull
	@Override
	public SocketChannel getChannel() {
		return channel;
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
		return zipTemplateAsync(template).getOrDefault(20, TimeUnit.SECONDS, null);
	}

	@Nonnull
	@Override
	public Task<Collection<ServiceTemplate>> getTemplatesAsync() {
		return sendRequest(
			RequestType.GET_TEMPLATES,
			buffer -> buffer.writeString(name),
			buffer -> throwException(buffer).readObjectCollection(ServiceTemplate.class)
		);
	}

	@Override
	@Nonnull
	public Task<InputStream> zipTemplateAsync(@Nonnull ServiceTemplate template) {
		Preconditions.checkNotNull(template);
		Preconditions.checkArgument(template.getStorage().equals(name), "The given ServiceTemplate must be from this TemplateStorage");

		return sendChunkedRequest(
			RequestType.LOAD_TEMPLATE_STREAM,
			buffer -> buffer.writeString(name).writeString(template.getName())
		).map(chunkedResponse -> {
			RequestResponseType response = chunkedResponse.getSession().getHeader().get("response", RequestResponseType.class);
			throwException(response, () -> Buffer.readAll(chunkedResponse.getInputStream()).readThrowable());
			return chunkedResponse.getInputStream();
		});
	}

	@Nonnull
	protected Buffer throwException(@Nonnull Buffer buffer) {
		throwException(buffer.readEnumConstant(RequestResponseType.class), buffer::readThrowable);
		return buffer;
	}

	protected void throwException(@Nonnull RequestResponseType response, @Nonnull Supplier<Throwable> supplier) {
		switch (response) {
			case EXCEPTION:
				Throwable ex = supplier.get();
				if (ex == null) throw new IllegalStateException("Request returned EXCEPTION response");
				throw new WrappedException(ex);
			case NOT_FOUND:
				throw new IllegalArgumentException("Remote TemplateStorage " + name + " was not found");
		}
	}

	@Override
	public boolean hasTemplate(@Nonnull ServiceTemplate template) {
		return hasTemplateAsync(template).getBeforeTimeout(5, TimeUnit.SECONDS);
	}

	@Nonnull
	@Override
	public Task<Boolean> hasTemplateAsync(@Nonnull ServiceTemplate template) {
		return sendRequest(
			RequestType.HAS_TEMPLATE,
			buffer -> buffer.writeObject(template),
			buffer -> buffer.readBoolean()
		);
	}

	@Override
	public String toString() {
		return "RemoteTemplateStorage[" + name + "]";
	}
}
