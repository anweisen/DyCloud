package net.anweisen.cloud.wrapper.config;

import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class WrapperConfig {

	private final HostAndPort masterAddress = HostAndPort.parse(System.getenv("cloud.config.master"));
	private final UUID identity = UUID.fromString(System.getenv("cloud.config.identity"));
	private final UUID uniqueId = UUID.fromString(System.getenv("cloud.service.uuid"));
	private final String nodeName = System.getenv("cloud.service.node");
	private final String name = System.getenv("cloud.service.name");
	private final int number = Integer.parseInt(name.split("-")[1]);
	private final ServiceTask task = Document.parseJson(System.getenv("cloud.service.task")).toInstanceOf(ServiceTask.class);

	@Nonnull
	public UUID getIdentity() {
		return identity;
	}

	@Nonnull
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public String getNodeName() {
		return nodeName;
	}

	@Nonnull
	public ServiceTask getTask() {
		return task;
	}

	public int getServiceNumber() {
		return number;
	}

	@Nonnull
	public HostAndPort getMasterAddress() {
		return masterAddress;
	}
}
