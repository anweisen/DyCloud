package net.anweisen.cloud.node.config;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NodeConfig {

	public static final File FILE = new File("config.json");
	public static final int DEFAULT_PORT = 3507;

	private FileDocument document;

	private String nodeName;
	private UUID identity;
	private HostAndPort masterAddress;
	private String dockerHost;

	public void load() {

		document = FileDocument.readJsonFile(FILE);

		nodeName = document.getString("name");
		if (nodeName ==  null)
			document.set("nodeName", nodeName = "Node-1");

		identity = document.getUUID("identity");
		if (identity == null)
			document.set("identity", identity = UUID.randomUUID());

		masterAddress = document.getDocument("masterAddress").toInstanceOf(HostAndPort.class);
		if (masterAddress == null)
			document.set("masterAddress", masterAddress = HostAndPort.localhost(DEFAULT_PORT));

		dockerHost = document.getString("dockerHost");
		if (dockerHost == null)
			document.set("dockerHost", dockerHost = "tcp://localhost:2375");

		document.save();
	}

	@Nonnull
	public UUID getIdentity() {
		return identity;
	}

	@Nonnull
	public String getNodeName() {
		return nodeName;
	}

	@Nonnull
	public String getDockerHost() {
		return dockerHost;
	}

	@Nonnull
	public HostAndPort getMasterAddress() {
		return masterAddress;
	}

}
