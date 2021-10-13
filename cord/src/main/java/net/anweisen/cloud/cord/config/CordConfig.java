package net.anweisen.cloud.cord.config;

import net.anweisen.cloud.cord.CloudCord;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudCord#getConfig()
 */
public final class CordConfig {

	private static final Path path = Paths.get("config.json");

	private HostAndPort bindAddress;

	public void load() {

		FileDocument document = FileDocument.readJsonFile(path);

		if (bindAddress == null)
			document.set("bindAddress", bindAddress = HostAndPort.localhost(25565));

		document.save();
	}

	@Nonnull
	public HostAndPort getBindAddress() {
		return bindAddress;
	}

}
