package net.anweisen.cloud.master.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.DriverConfig;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.driver.network.object.SSLConfiguration;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;
import net.anweisen.utility.document.wrapped.StorableDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudMaster#getConfig()
 */
public final class MasterConfig implements DriverConfig, LoggingApiUser {

	private static final Path path = Paths.get("config.json");

	private UUID identity;
	private HostAndPort hostAddress;
	private SSLConfiguration webSslConfig;
	private Collection<HostAndPort> httpListeners;
	private Collection<String> ipWhitelist;
	private Document databaseConfig;

	@Override
	public void load() throws IOException {

		StorableDocument document = Documents.newStorableJsonDocument(path);

		identity = document.getUniqueId("identity");
		if (identity == null)
			document.set("identity", identity = UUID.randomUUID());

		hostAddress = document.getInstance("address", HostAndPort.class);
		if (hostAddress == null)
			document.set("address", hostAddress = HostAndPort.localhost(CloudDriver.DEFAULT_PORT));

		if (!document.contains("httpListeners"))
			document.set("httpListeners", Collections.singletonList(HostAndPort.localhost(CloudDriver.DEFAULT_HTTP_PORT)));
		httpListeners = document.getBundle("httpListeners").toInstances(HostAndPort.class);

		if (!document.contains("webSsl"))
			document.getDocument("webSsl")
				.set("enabled", false)
				.set("clientAuth", false)
				.set("certificatePath", "/etc/ssl/certificate.pem")
				.set("privateKeyPath", "/etc/ssl/privateKey.key");
		if (document.getDocument("webSsl").getBoolean("enabled")) {
			Document sslDocument = document.getDocument("webSsl");
			String certificate = sslDocument.getString("certificatePath");
			String privateKey = sslDocument.getString("privateKeyPath");
			webSslConfig = new SSLConfiguration(
				sslDocument.getBoolean("clientAuth"),
				certificate == null ? null : Paths.get(certificate),
				privateKey == null ? null : Paths.get(privateKey)
			);
		}

		ipWhitelist = document.getBundle("ipWhitelist").toStrings();
		if (!document.contains("ipWhitelist"))
			document.set("ipWhitelist", Collections.singletonList(HostAndPort.localhost()));

		if (!document.contains("database"))
			document.getDocument("database")
				.set("type", "sqlite")
				.set("config.file", "database.db")
				.set("config.host", "127.0.0.1")
				.set("config.port", 23764587)
				.set("config.database", "cloud")
				.set("config.auth-database", "admin")
				.set("config.user", "root")
				.set("config.password", "secret");
		databaseConfig = document.getDocument("database");

		Document startPorts = document.getDocument("startPorts");
		for (ServiceType type : ServiceType.values()) {
			if (!startPorts.contains(type.name())) {
				startPorts.set(type.name(), type.getStartPort());
			} else {
				type.setStartPort(startPorts.getInt(type.name()));
			}

			extended("=> Startport for {} = {}", type, type.getStartPort());
		}

		document.save();
	}

	@Nonnull
	@Override
	public UUID getIdentity() {
		return identity;
	}

	@Nonnull
	public HostAndPort getHostAddress() {
		return hostAddress;
	}

	@Nullable
	public SSLConfiguration getWebSslConfig() {
		return webSslConfig;
	}

	@Nonnull
	public Collection<HostAndPort> getHttpListeners() {
		return httpListeners;
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
