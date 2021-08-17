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

	private String nodeName;
	private UUID identity;
	private HostAndPort masterAddress;
	private String dockerHost;
	private String dockerNetworkMode;

	public void load() {

		FileDocument document = FileDocument.readJsonFile(FILE);

		nodeName = document.getString("name");
		if (nodeName ==  null)
			document.set("nodeName", nodeName = "Node-1");

		identity = document.getUUID("identity");
		if (identity == null)
			document.set("identity", identity = UUID.randomUUID());

		masterAddress = document.getDocument("masterAddress").toInstanceOf(HostAndPort.class);
		if (masterAddress == null)
			document.set("masterAddress", masterAddress = HostAndPort.localhost(DEFAULT_PORT));

		dockerHost = document.getString("docker.host");
		if (dockerHost == null)
			document.set("docker.host", dockerHost = "tcp://localhost:2375");

		dockerNetworkMode = document.getString("docker.networkMode");
		if (dockerNetworkMode == null)
			document.set("docker.networkMode", dockerNetworkMode = "host");

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
	public String getDockerNetworkMode() {
		return dockerNetworkMode;
	}

	@Nonnull
	public HostAndPort getMasterAddress() {
		return masterAddress;
	}

}
