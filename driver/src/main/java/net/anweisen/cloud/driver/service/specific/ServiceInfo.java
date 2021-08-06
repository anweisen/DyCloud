package net.anweisen.cloud.driver.service.specific;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.driver.service.config.ServiceTask;

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

	private String nodeName;
	private ServiceState state;

	private int port;

	public ServiceInfo() {
	}

	public ServiceInfo(@Nonnull UUID uniqueId, @Nullable String dockerContainerId, @Nonnull String taskName, @Nonnegative int serviceNumber, @Nonnull String nodeName, int port, @Nonnull ServiceState state) {
		this.uniqueId = uniqueId;
		this.dockerContainerId = dockerContainerId;
		this.taskName = taskName;
		this.serviceNumber = serviceNumber;
		this.nodeName = nodeName;
		this.state = state;
		this.port = port;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(taskName);
		buffer.writeInt(serviceNumber);
		buffer.writeString(nodeName);
		buffer.writeEnumConstant(state);
		buffer.writeInt(port);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		taskName = buffer.readString();
		serviceNumber = buffer.readInt();
		nodeName = buffer.readString();
		state = buffer.readEnumConstant(ServiceState.class);
		port = buffer.readInt();
	}

	@Nonnull
	public String getName() {
		return this.taskName + "-" + this.serviceNumber;
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
	public ServiceState getState() {
		return state;
	}

	public int getPort() {
		return port;
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
		Preconditions.checkNotNull(state, "The given state is not null");
		this.state = state;
	}

	@Override
	public String toString() {
		return "Service[" + getName() + ":" + uniqueId + " containerId=" + dockerContainerId + " node=" + nodeName + " port=" + port + " state=" + state + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceInfo that = (ServiceInfo) o;
		return serviceNumber == that.serviceNumber && Objects.equals(uniqueId, that.uniqueId) && Objects.equals(dockerContainerId, that.dockerContainerId) && Objects.equals(taskName, that.taskName) && Objects.equals(nodeName, that.nodeName) && state == that.state;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, dockerContainerId, taskName, serviceNumber, nodeName, state);
	}
}
