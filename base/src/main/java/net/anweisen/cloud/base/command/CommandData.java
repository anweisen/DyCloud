package net.anweisen.cloud.base.command;

import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CommandData implements SerializableObject {

	private String[] name;
	private String path;
	private String permission;
	private CommandScope scope;

	private CommandData() {
	}

	public CommandData(@Nonnull String[] name, @Nonnull String path, @Nonnull String permission, @Nonnull CommandScope scope) {
		this.name = name;
		this.path = path;
		this.permission = permission;
		this.scope = scope;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeStringArray(name);
		buffer.writeString(path);
		buffer.writeString(permission);
		buffer.writeEnum(scope);
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		name = buffer.readStringArray();
		path = buffer.readString();
		permission = buffer.readString();
		scope = buffer.readEnum(CommandScope.class);
	}

	@Nonnull
	public String[] getName() {
		return name;
	}

	@Nonnull
	public String getPath() {
		return path;
	}

	@Nonnull
	public String getPermission() {
		return permission;
	}

	@Nonnull
	public CommandScope getScope() {
		return scope;
	}
}
