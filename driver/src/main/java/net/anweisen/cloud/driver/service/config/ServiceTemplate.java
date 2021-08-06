package net.anweisen.cloud.driver.service.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceTemplate implements SerializableObject {

	private String storage;
	private String name;

	public ServiceTemplate() {
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
}
