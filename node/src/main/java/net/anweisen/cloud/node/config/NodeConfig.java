package net.anweisen.cloud.node.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.RemoteConfig;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.node.CloudNode;
import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudNode#getConfig()
 */
public final class NodeConfig implements RemoteConfig {

	private static final Path path = Paths.get("config.json");

	private String nodeName;
	private UUID identity;
	private HostAndPort masterAddress;
	private String dockerHost;
	private String dockerNetworkMode;

	public void load() {

		FileDocument document = FileDocument.readJsonFile(path);

		nodeName = document.getString("name");
		if (nodeName ==  null)
			document.set("nodeName", nodeName = "Node-1");

		identity = document.getUUID("identity");
		if (identity == null)
			document.set("identity", identity = UUID.randomUUID());

		masterAddress = document.getDocument("masterAddress").toInstanceOf(HostAndPort.class);
		if (masterAddress == null)
			document.set("masterAddress", masterAddress = HostAndPort.localhost(CloudDriver.DEFAULT_PORT));

		dockerHost = document.getString("docker.host");
		if (dockerHost == null)
			document.set("docker.host", dockerHost = "tcp://localhost:2375");

		dockerNetworkMode = document.getString("docker.networkMode");
		if (dockerNetworkMode == null)
			document.set("docker.networkMode", dockerNetworkMode = "host");

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

}
