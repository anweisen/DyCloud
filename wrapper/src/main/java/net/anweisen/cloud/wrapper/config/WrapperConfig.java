package net.anweisen.cloud.wrapper.config;

import net.anweisen.cloud.driver.config.DriverRemoteConfig;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.wrapper.CloudWrapper;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudWrapper#getConfig()
 */
public final class WrapperConfig implements DriverRemoteConfig {

	private static final Path path = Paths.get(".cloud", "config.json");

	private HostAndPort masterAddress;
	private UUID identity;
	private UUID serviceUniqueId;
	private String serviceTaskName;

	@Override
	public void load() throws IOException {

		Document document = Documents.newJsonDocument(path);

		masterAddress = document.getInstance("master", HostAndPort.class);
		identity = document.getUniqueId("identity");
		serviceUniqueId = document.getUniqueId("serviceUniqueId");
		serviceTaskName = document.getString("serviceTaskName");

	}

	@Nonnull
	@Override
	public UUID getIdentity() {
		return identity;
	}

	@Nonnull
	@Override
	public HostAndPort getMasterAddress() {
		return masterAddress;
	}

	@Nonnull
	public UUID getServiceUniqueId() {
		return serviceUniqueId;
	}

	@Nonnull
	public String getServiceTaskName() {
		return serviceTaskName;
	}
}
