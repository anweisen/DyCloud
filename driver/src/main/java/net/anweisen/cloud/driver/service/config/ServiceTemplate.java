package net.anweisen.cloud.driver.service.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ServiceTemplate implements SerializableObject {

	@Nonnull
	public static ServiceTemplate getDefault(@Nonnull String name) {
		return new ServiceTemplate("default", name);
	}

	private String storage;
	private String name;

	private ServiceTemplate() {
	}

	public ServiceTemplate(@Nonnull String storage, @Nonnull String name) {
		this.storage = storage;
		this.name = name;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(storage);
		buffer.writeString(name);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		storage = buffer.readString();
		name = buffer.readString();
	}

	@Nonnull
	public String getStorage() {
		return storage;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nullable
	public TemplateStorage findStorage() {
		return CloudDriver.getInstance().getServiceConfigManager().getTemplateStorage(storage);
	}

	@Nonnull
	@Override
	public String toString() {
		return "ServiceTemplate[" + toShortString() + "]";
	}

	@Nonnull
	public String toShortString() {
		return storage + "/" + name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceTemplate that = (ServiceTemplate) o;
		return Objects.equals(storage, that.storage) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(storage, name);
	}
}
