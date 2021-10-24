package net.anweisen.cloud.driver.config.global.objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CommandObject {

	private String path;
	private String permission;

	private CommandObject() {
	}

	public CommandObject(@Nonnull String path, @Nullable String permission) {
		this.path = path;
		this.permission = permission;
	}

	@Nonnull
	public String getPath() {
		return path;
	}

	@Nullable
	public String getPermission() {
		return permission;
	}

	@Override
	public String toString() {
		return path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommandObject that = (CommandObject) o;
		return Objects.equals(path, that.path) && Objects.equals(permission, that.permission);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, permission);
	}
}
