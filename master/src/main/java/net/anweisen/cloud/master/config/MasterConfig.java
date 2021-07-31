package net.anweisen.cloud.master.config;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterConfig {

	public static final File FILE = new File("config.json");
	public static final int DEFAULT_PORT = 3507;

	private FileDocument document;

	private UUID identity;
	private HostAndPort hostAddress;
	private Collection<String> ipWhitelist;
	private Document databaseConfig;

	public void load() {

		document = FileDocument.readJsonFile(FILE);

		identity = document.getUUID("identity");
		if (identity == null)
			document.set("identity", identity = UUID.randomUUID());

		hostAddress = document.get("address", HostAndPort.class);
		if (hostAddress == null)
			document.set("address", hostAddress = HostAndPort.localhost(DEFAULT_PORT));

		ipWhitelist = document.getStringList("ipWhitelist");
		if (!document.contains("ipWhitelist"))
			document.set("ipWhitelist", Collections.singletonList(HostAndPort.localhost()));

		if (!document.contains("database"))
			document.set("database.type", "sqlite")
					.set("database.config.file", "database.db")
					.set("database.config.host", "127.0.0.1")
					.set("database.config.database", "cloud")
					.set("database.config.auth-database", "admin")
					.set("database.config.user", "root")
					.set("database.config.password", "secret");
		databaseConfig = document.getDocument("database");

		document.save();
	}

	@Nonnull
	public UUID getIdentity() {
		return identity;
	}

	@Nonnull
	public HostAndPort getHostAddress() {
		return hostAddress;
	}

	@Nonnull
	public Collection<String> getIpWhitelist() {
		return ipWhitelist;
	}

	@Nonnull
	public Document getDatabaseConfig() {
		return databaseConfig;
	}
}
