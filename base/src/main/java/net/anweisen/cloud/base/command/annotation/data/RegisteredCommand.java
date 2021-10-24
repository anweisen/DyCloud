package net.anweisen.cloud.base.command.annotation.data;

import net.anweisen.cloud.base.command.CommandScope;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class RegisteredCommand {

	private final String[] name;
	private final String path;
	private final String permission;
	private final CommandScope scope;
	private final List<RegisteredCommandArgument> arguments;
	private final Method method;
	private final Object instance;

	public RegisteredCommand(@Nonnull String[] name, @Nonnull String path, @Nonnull String permission, @Nonnull CommandScope scope,
	                         @Nonnull List<RegisteredCommandArgument> arguments, @Nonnull Method method, @Nonnull Object instance) {
		this.name = name;
		this.path = path;
		this.permission = permission;
		this.scope = scope;
		this.method = method;
		this.instance = instance;
		this.arguments = arguments;
	}

	@Nonnull
	public String[] getNames() {
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

	@Nonnull
	public List<RegisteredCommandArgument> getArguments() {
		return arguments;
	}

	@Nonnull
	public RegisteredCommandArgument getArgument(@Nonnull String name) {
		for (RegisteredCommandArgument argument : arguments) {
			if (argument.getName().equalsIgnoreCase(name))
				return argument;
		}
		throw new IllegalStateException("No such argument: " + name + " in " + this);
	}

	@Nonnull
	public Method getMethod() {
		return method;
	}

	@Nonnull
	public Object getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "{" + "name=" + Arrays.toString(name) + " path='" + path + '\'' + " permission='" + permission + "' scope=" + scope + '}';
	}
}
