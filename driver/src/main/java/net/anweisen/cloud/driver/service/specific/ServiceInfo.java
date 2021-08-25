package net.anweisen.cloud.driver.service.specific;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ServiceInfo implements SerializableObject {

	private UUID uniqueId;
	private String dockerContainerId;

	private String taskName;
	private int serviceNumber;

	private ServiceEnvironment environment;
	private ServiceState state;
	private boolean ready;

	private String nodeName;
	private String nodeAddress;

	private int port;
	private boolean staticService;

	private Document properties;

	private ServiceInfo() {
	}

	public ServiceInfo(@Nonnull UUID uniqueId, @Nullable String dockerContainerId, @Nonnull String taskName, @Nonnegative int serviceNumber, @Nonnull ServiceEnvironment environment,
	                   @Nonnull ServiceState state, @Nonnull String nodeName, @Nonnull String nodeAddress, int port, boolean staticService, @Nonnull Document properties) {
		this.uniqueId = uniqueId;
		this.dockerContainerId = dockerContainerId;
		this.taskName = taskName;
		this.serviceNumber = serviceNumber;
		this.environment = environment;
		this.state = state;
		this.nodeName = nodeName;
		this.nodeAddress = nodeAddress;
		this.port = port;
		this.staticService = staticService;
		this.properties = properties;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(taskName);
		buffer.writeInt(serviceNumber);
		buffer.writeString(nodeName);
		buffer.writeString(nodeAddress);
		buffer.writeEnumConstant(state);
		buffer.writeBoolean(ready);
		buffer.writeEnumConstant(environment);
		buffer.writeInt(port);
		buffer.writeBoolean(staticService);
		buffer.writeDocument(properties);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		taskName = buffer.readString();
		serviceNumber = buffer.readInt();
		nodeName = buffer.readString();
		nodeAddress = buffer.readString();
		state = buffer.readEnumConstant(ServiceState.class);
		ready = buffer.readBoolean();
		environment = buffer.readEnumConstant(ServiceEnvironment.class);
		port = buffer.readInt();
		staticService = buffer.readBoolean();
		properties = buffer.readDocument();
	}

	@Nonnull
	public String getName() {
		return taskName + "-" + serviceNumber;
	}

	@Nonnull
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Nullable
	public String getDockerContainerId() {
		return dockerContainerId;
	}

	@Nonnull
	public String getTaskName() {
		return taskName;
	}

	@Nonnegative
	public int getServiceNumber() {
		return serviceNumber;
	}

	@Nonnull
	public String getNodeName() {
		return nodeName;
	}

	@Nonnull
	public String getNodeAddress() {
		return nodeAddress;
	}

	@Nonnull
	public ServiceState getState() {
		return state;
	}

	public boolean isReady() {
		return state == ServiceState.RUNNING && ready;
	}

	@Nonnull
	public ServiceEnvironment getEnvironment() {
		return environment;
	}

	public int getPort() {
		return port;
	}

	public boolean isStatic() {
		return staticService;
	}

	@Nonnull
	public Document getProperties() {
		return properties;
	}

	@Nonnull
	public HostAndPort getAddress() {
		return new HostAndPort(nodeAddress, port);
	}

	@Nullable
	public NodeInfo findNode() {
		return CloudDriver.getInstance().getNodeManager().getNodeInfo(nodeName);
	}

	@Nullable
	public ServiceTask findTask() {
		return CloudDriver.getInstance().getServiceConfigManager().getTask(taskName);
	}

	public void setDockerContainerId(@Nonnull String dockerContainerId) {
		Preconditions.checkNotNull(dockerContainerId, "The given id cannot be null");
		this.dockerContainerId = dockerContainerId;
	}

	public void setState(@Nonnull ServiceState state) {
		Preconditions.checkNotNull(state, "The given state cannot be null");
		this.state = state;
	}

	public void setReady() {
		Preconditions.checkArgument(state == ServiceState.RUNNING, "Cannot set service ready when not in State.RUNNING");
		this.ready = true;
	}

	@Override
	public String toString() {
		return "Service[name=" + getName() + " node=" + nodeName + " port=" + port + " state=" + state + (state == ServiceState.RUNNING ? ":" + (ready ? "ready" : "unready") : "") + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceInfo that = (ServiceInfo) o;
		return serviceNumber == that.serviceNumber
			&& ready == that.ready
			&& port == that.port
			&& staticService == that.staticService
			&& environment == that.environment
			&& state == that.state
			&& Objects.equals(uniqueId, that.uniqueId)
			&& Objects.equals(dockerContainerId, that.dockerContainerId)
			&& Objects.equals(taskName, that.taskName)
			&& Objects.equals(nodeName, that.nodeName)
			&& Objects.equals(nodeAddress, that.nodeAddress)
			&& Objects.equals(properties, that.properties);
	}

	public boolean isSameService(@Nullable ServiceInfo that) {
		return that != null
			&& uniqueId.equals(that.uniqueId)
			&& taskName.equals(that.taskName)
			&& serviceNumber == that.serviceNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, dockerContainerId, taskName, serviceNumber, environment, state, ready, nodeName, nodeAddress, port, staticService, properties);
	}
}
