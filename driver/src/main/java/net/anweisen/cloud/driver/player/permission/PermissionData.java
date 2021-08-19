package net.anweisen.cloud.driver.player.permission;

import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class PermissionData implements SerializableObject {

	public static final class TaskPermissionData implements SerializableObject {

		private String name;
		private String task;

		private TaskPermissionData() {
		}

		public TaskPermissionData(@Nonnull String name, @Nonnull String task) {
			this.name = name;
			this.task = task;
		}

		@Override
		public void write(@Nonnull Buffer buffer) {
			buffer.writeString(name);
			buffer.writeString(task);
		}

		@Override
		public void read(@Nonnull Buffer buffer) {
			name = buffer.readString();
			task = buffer.readString();
		}

		@Nonnull
		public String getName() {
			return name;
		}

		@Nonnull
		public String getTask() {
			return task;
		}

	}

	public static final class PlayerGroupData implements SerializableObject {

		private String name;
		private long timeout;

		private PlayerGroupData() {
		}

		public PlayerGroupData(@Nonnull String name, long timeout) {
			this.name = name;
			this.timeout = timeout;
		}

		@Override
		public void write(@Nonnull Buffer buffer) {
			buffer.writeString(name);
			buffer.writeLong(timeout);
		}

		@Override
		public void read(@Nonnull Buffer buffer) {
			name = buffer.readString();
			timeout = buffer.readLong();
		}

		@Nonnull
		public String getName() {
			return name;
		}

		public long getTimeoutTime() {
			return timeout;
		}

	}

	private Collection<String> permissions;
	private Collection<TaskPermissionData> taskPermissions;
	private Collection<PlayerGroupData> groups;

	private PermissionData() {
	}

	public PermissionData(@Nonnull Collection<String> permissions, @Nonnull Collection<TaskPermissionData> taskPermissions, @Nonnull Collection<PlayerGroupData> groups) {
		this.permissions = permissions;
		this.taskPermissions = taskPermissions;
		this.groups = groups;
	}

	@Nonnull
	public Collection<String> getPermissions() {
		return permissions;
	}

	@Nonnull
	public Collection<TaskPermissionData> getTaskPermissions() {
		return taskPermissions;
	}

	@Nonnull
	public Collection<PlayerGroupData> getGroups() {
		return groups;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeStringCollection(permissions);
		buffer.writeObjectCollection(taskPermissions);
		buffer.writeObjectCollection(groups);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		permissions = buffer.readStringCollection();
		taskPermissions = buffer.readObjectCollection(TaskPermissionData.class);
		groups = buffer.readObjectCollection(PlayerGroupData.class);
	}

}
