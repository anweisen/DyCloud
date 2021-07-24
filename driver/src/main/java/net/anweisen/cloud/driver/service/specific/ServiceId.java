package net.anweisen.cloud.driver.service.specific;

import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ServiceId implements SerializableObject {

	protected UUID uniqueId;

	protected String taskName;
	protected int serviceNumber;

	protected String nodeName;

	public ServiceId(@Nonnull UUID uniqueId, @Nonnull String taskName, @Nonnegative int serviceNumber, @Nonnull String nodeName) {
		this.uniqueId = uniqueId;
		this.taskName = taskName;
		this.serviceNumber = serviceNumber;
		this.nodeName = nodeName;
	}

	public ServiceId() {
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeUUID(uniqueId);
		buffer.writeString(taskName);
		buffer.writeInt(serviceNumber);
		buffer.writeString(nodeName);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		uniqueId = buffer.readUUID();
		taskName = buffer.readString();
		serviceNumber = buffer.readInt();
		nodeName = buffer.readString();
	}

	@Nonnull
	public String getName() {
		return this.taskName + "-" + this.serviceNumber;
	}

	@Nonnull
	public UUID getUniqueId() {
		return uniqueId;
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

	@Override
	public String toString() {
		return "ServiceId[" + getName() + ":" + uniqueId + " node=" + nodeName + "]";
	}
}
