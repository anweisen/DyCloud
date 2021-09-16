package net.anweisen.cloud.cord.config;

import net.anweisen.cloud.cord.CloudCord;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.DriverRemoteConfig;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudCord#getConfig()
 */
public final class CordConfig implements DriverRemoteConfig {

	private static final Path path = Paths.get("config.json");

	private String cordName;
	private UUID identity;
	private HostAndPort masterAddress;
	private HostAndPort bindAddress;

	@Override
	public void load() {

		FileDocument document = FileDocument.readJsonFile(path);

		cordName = document.getString("name");
		if (cordName == null)
			document.set("name", cordName = "Cord-1");

		identity = document.getUUID("identity");
		if (identity == null)
			document.set("identity", identity = UUID.randomUUID());

		masterAddress = document.getDocument("masterAddress").toInstanceOf(HostAndPort.class);
		if (masterAddress == null)
			document.set("masterAddress", masterAddress = HostAndPort.localhost(CloudDriver.DEFAULT_PORT));

		bindAddress = document.getDocument("bindAddress").toInstanceOf(HostAndPort.class);
		if (bindAddress == null)
			document.set("bindAddress", bindAddress = HostAndPort.localhost(25565));

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

	@Nonnull
	public HostAndPort getBindAddress() {
		return bindAddress;
	}

	@Nonnull
	public String getCordName() {
		return cordName;
	}
}
