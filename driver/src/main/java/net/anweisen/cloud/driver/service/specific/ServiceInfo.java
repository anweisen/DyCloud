package net.anweisen.cloud.driver.service.specific;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.object.HostAndPort;
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

	public static final int PUBLISH_INTERVAL = 90_000; // publish all 1.5 minute
	public static final int CYCLE_TIMEOUT = 2; // service times out after 3 minutes

	private UUID uniqueId;
	private String dockerContainerId;

	private String taskName;
	private int serviceNumber;

	private ServiceEnvironment environment;
	private ServiceState state;
	private ServiceControlState controlState;
	private boolean ready;

	private String nodeName;
	private String nodeAddress;

	private int port;
	private boolean permanent;

	private long timestamp;

	private Document properties;

	private ServiceInfo() {
	}

	public ServiceInfo(@Nonnull UUID uniqueId, @Nullable String dockerContainerId, @Nonnull String taskName, @Nonnegative int serviceNumber, @Nonnull ServiceEnvironment environment,
	                   @Nonnull ServiceState state, @Nonnull ServiceControlState controlState, @Nonnull String nodeName, @Nonnull String nodeAddress, int port, boolean permanent,
	                   @Nonnull Document properties) {
		this.uniqueId = uniqueId;
		this.dockerContainerId = dockerContainerId;
		this.taskName = taskName;
		this.serviceNumber = serviceNumber;
		this.environment = environment;
		this.state = state;
		this.controlState = controlState;
		this.nodeName = nodeName;
		this.nodeAddress = nodeAddress;
		this.port = port;
		this.permanent = permanent;
		this.properties = properties;
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(taskName);
		buffer.writeInt(serviceNumber);
		buffer.writeString(nodeName);
		buffer.writeString(nodeAddress);
		buffer.writeEnumConstant(state);
		buffer.writeEnumConstant(controlState);
		buffer.writeOptionalString(dockerContainerId);
		buffer.writeBoolean(ready);
		buffer.writeEnumConstant(environment);
		buffer.writeInt(port);
		buffer.writeBoolean(permanent);
		buffer.writeDocument(properties);
		buffer.writeLong(timestamp);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		taskName = buffer.readString();
		serviceNumber = buffer.readInt();
		nodeName = buffer.readString();
		nodeAddress = buffer.readString();
		state = buffer.readEnumConstant(ServiceState.class);
		controlState = buffer.readEnumConstant(ServiceControlState.class);
		dockerContainerId = buffer.readOptionalString();
		ready = buffer.readBoolean();
		environment = buffer.readEnumConstant(ServiceEnvironment.class);
		port = buffer.readInt();
		permanent = buffer.readBoolean();
		properties = buffer.readDocument();
		timestamp = buffer.readLong();
	}

	@Nonnull
	public ServiceController getController() {
		return CloudDriver.getInstance().getServiceManager().getController(this);
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

	@Nonnull
	public ServiceControlState getControlState() {
		return controlState;
	}

	/**
	 * The service is marked as ready by the bridge plugin when it was enabled.
	 * If the server software does not have a bridge plugin it will always be {@code true} if the service is running.
	 */
	public boolean isReady() {
		return state == ServiceState.RUNNING && (ready || !environment.hasBridge());
	}

	@Nonnull
	public ServiceEnvironment getEnvironment() {
		return environment;
	}

	public int getPort() {
		return port;
	}

	public boolean isPermanent() {
		return permanent;
	}

	@Nonnull
	public Document getProperties() {
		return properties;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public <T> T get(@Nonnull ServiceProperty<T> property) {
		return property.getProperty(properties);
	}

	@Nonnull
	public <T> ServiceInfo set(@Nonnull ServiceProperty<T> property, T value) {
		property.setProperty(properties, value);
		return this;
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

	public void setControlState(@Nonnull ServiceControlState controlState) {
		Preconditions.checkNotNull(controlState, "The given control state cannot be null");
		this.controlState = controlState;
	}

	public void setReady() {
		Preconditions.checkArgument(state == ServiceState.RUNNING, "Cannot mark service as ready when not in State.RUNNING");
		this.ready = true;
	}

	@Override
	public String toString() {
		return "Service[name=" + getName() + " node=" + nodeName + " port=" + port
					+ " state=" + state + (state == ServiceState.RUNNING ? ":" + (ready ? "ready" : "unready") : "") + (controlState != ServiceControlState.NONE ? "->" + controlState : "") + "]";
	}

	public String toFullString() {
		return "Service[" +
			"\n  uniqueId=" + uniqueId +
			"\n  name=" + getName() +
			"\n  dockerContainerId=" + dockerContainerId +
			"\n  environment=" + environment +
			"\n  node=" + nodeName +
			"\n  port=" + port +
			"\n  address=" + nodeAddress +
			"\n  state=" + state + ":" + (ready ? "ready" : "unready") + "->" + controlState +
			"\n  properties=" + properties.toPrettyJson() +
			"\n]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceInfo that = (ServiceInfo) o;
		return serviceNumber == that.serviceNumber
			&& ready == that.ready
			&& port == that.port
			&& permanent == that.permanent
			&& environment == that.environment
			&& state == that.state
			&& controlState == that.controlState
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
		return Objects.hash(uniqueId, dockerContainerId, taskName, serviceNumber, environment, state, controlState, ready, nodeName, nodeAddress, port, permanent, properties);
	}
}
