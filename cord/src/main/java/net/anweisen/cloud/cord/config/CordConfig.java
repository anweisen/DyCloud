package net.anweisen.cloud.cord.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.RemoteConfig;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CordConfig implements RemoteConfig {

	private static final Path path = Paths.get("config.json");

	private UUID identity;
	private HostAndPort masterAddress;

	@Override
	public void load() {

		FileDocument document = FileDocument.readJsonFile(path);

		identity = document.getUUID("identity");
		if (identity == null)
			document.set("identity", identity = UUID.randomUUID());

		masterAddress = document.getDocument("masterAddress").toInstanceOf(HostAndPort.class);
		if (masterAddress == null)
			document.set("masterAddress", masterAddress = HostAndPort.localhost(CloudDriver.DEFAULT_PORT));

		document.save();
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
}
