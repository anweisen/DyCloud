package net.anweisen.cloud.wrapper.config;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class WrapperConfig {

	private static final Path configPath = Paths.get(".cloud", "config.json");

	private HostAndPort masterAddress;
	private UUID identity;
	private ServiceTask task;
	private ServiceInfo serviceInfo;

	public void load() {

		Document document = Document.readJsonFile(configPath.toFile());

		masterAddress = document.get("master", HostAndPort.class);
		identity = document.getUUID("identity");
		task = document.get("task", ServiceTask.class);
		serviceInfo = document.get("service", ServiceInfo.class);

	}

	@Nonnull
	public UUID getIdentity() {
		return identity;
	}

	@Nonnull
	public ServiceTask getTask() {
		return task;
	}

	@Nonnull
	public HostAndPort getMasterAddress() {
		return masterAddress;
	}

	@Nonnull
	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}
}
