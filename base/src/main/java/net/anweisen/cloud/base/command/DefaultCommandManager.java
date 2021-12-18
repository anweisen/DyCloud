package net.anweisen.cloud.base.command;

import net.anweisen.cloud.base.command.annotation.Command;
import net.anweisen.cloud.base.command.annotation.CommandArgument;
import net.anweisen.cloud.base.command.annotation.CommandPath;
import net.anweisen.cloud.base.command.annotation.data.RegisteredCommand;
import net.anweisen.cloud.base.command.annotation.data.RegisteredCommandArgument;
import net.anweisen.cloud.base.command.completer.CommandCompleter;
import net.anweisen.cloud.base.command.sender.CommandSender;
import net.anweisen.cloud.base.command.sender.PlayerCommandSender;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.config.global.objects.CommandObject;
import net.anweisen.utility.common.collection.WrappedException;
import net.anweisen.utility.common.misc.ReflectionUtils;
import net.anweisen.utility.common.misc.StringUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultCommandManager implements CommandManager {

	private final Map<Class<? extends CommandCompleter>, CommandCompleter> completer = new HashMap<>();
	private final Collection<RegisteredCommand> commands = new CopyOnWriteArrayList<>();

	@Override
	public void registerCommand(@Nonnull Object command) {
		Command commandAnnotation = command.getClass().getAnnotation(Command.class);

		for (Method method : ReflectionUtils.getMethodsAnnotatedWith(command.getClass(), CommandPath.class)) {
			CommandPath pathAnnotation = method.getAnnotation(CommandPath.class);

			List<RegisteredCommandArgument> arguments = new ArrayList<>();
			for (Parameter parameter : method.getParameters()) {
				if (!parameter.isAnnotationPresent(CommandArgument.class)) continue;
				CommandArgument argumentAnnotation = parameter.getAnnotation(CommandArgument.class);
				arguments.add(new RegisteredCommandArgument(argumentAnnotation.value(), argumentAnnotation.completer(), argumentAnnotation.words(), argumentAnnotation.raw(), argumentAnnotation.optional()));
			}

			commands.add(new RegisteredCommand(
				commandAnnotation.name(), pathAnnotation.value(), commandAnnotation.permission(), commandAnnotation.scope(),
				arguments, method, command
			));
		}

		handleCommandChange();
	}

	@Override
	public void unregisterCommand(@Nonnull Object instance) {
		commands.removeIf(command -> command.getInstance() == instance);
		handleCommandChange();
	}

	@Override
	public void unregisterCommand(@Nonnull ClassLoader classLoader) {
		commands.removeIf(command -> command.getInstance().getClass().getClassLoader() == classLoader);
		handleCommandChange();
	}

	@Override
	public void unregisterCommand(@Nonnull String name) {
		commands.removeIf(command -> Arrays.asList(command.getNames()).contains(name));
		handleCommandChange();
	}

	protected abstract void handleCommandChange();

	public void updateIngameCommands() {
		Set<CommandObject> ingameCommands = new HashSet<>();
		for (RegisteredCommand command : commands) {
			if (command.getScope().isIngame()) {
				String prefix = command.getScope().hasCloudPrefix() ? "cloud " : "";
				for (String name : command.getNames()) {
					ingameCommands.add(new CommandObject(prefix + name, command.getPermission()));
				}
			}
		}

		CloudDriver.getInstance().getLogger().debug("Ingame Commands: {}", ingameCommands);
		CloudDriver.getInstance().getGlobalConfig().setIngameCommands(ingameCommands).update();
	}

	@Nonnull
	@Override
	public Collection<RegisteredCommand> getCommands() {
		return Collections.unmodifiableCollection(commands);
	}

	@Nonnull
	public Collection<RegisteredCommand> matchCommandName(@Nonnull String usedName) {
		Collection<RegisteredCommand> matching = new ArrayList<>();
		for (RegisteredCommand command : commands) {
			for (String name : command.getNames()) {
				if (usedName.equals(name)) {
					matching.add(command);
					break;
				}
			}
		}
		return matching;
	}

	@Nonnull
	@Override
	public Collection<String> completeCommand(@Nonnull CommandSender sender, @Nonnull String input) {
		String lowered = input.toLowerCase();

		Collection<String> suggestions = new CopyOnWriteArraySet<>();
		command: for (RegisteredCommand command : commands) {
			if (!command.getScope().covers(sender)) continue;
			if (!command.getPermission().isEmpty() && !sender.hasPermission(command.getPermission())) continue;

			// if the command begins with the cloud prefix, remove it
			String stripped = lowered;
			if (command.getScope().hasCloudPrefix() && sender instanceof PlayerCommandSender) {
				if (lowered.startsWith("cloud"))
					stripped = lowered.substring("cloud".length());
				while (stripped.startsWith(" "))
					stripped = stripped.substring(1);
			}

			for (String name : command.getNames()) {
				boolean startsWithName = stripped.startsWith(name);
				boolean startsWithInput = name.startsWith(stripped);
				if (!startsWithName && !startsWithInput) continue;
				if (!startsWithName || name.equalsIgnoreCase(stripped)) {
					suggestions.add(name);
					continue;
				}

				// the current alt name of the command was used
				String remaining = stripped.substring(name.length());
				// check whether this name was really used: input: player, name: players -> incorrect
				if (!remaining.startsWith(" ") && !remaining.isEmpty()) continue;

				while (remaining.startsWith(" "))
					remaining = remaining.substring(1);

				String[] args = remaining.split(" ");
				if (remaining.endsWith(" ")) {
					// we want to get the next argument
					args = Arrays.copyOf(args, args.length + 1);
					args[args.length - 1] = "";
				}

				String path = command.getPath();
				String[] pathArgs = path.split(" ");
				for (int p = 0, a = 0; p < pathArgs.length; p++, a++) {
					// p: the current path args index
					// a: the current given args index
					// d: the current dynamic argument index

					String currentPathArg = pathArgs[p];
					String currentGivenArg = args[a];
					boolean dynamicArg = currentPathArg.startsWith("<") && currentPathArg.endsWith(">");
					if (dynamicArg) {
						RegisteredCommandArgument argument = command.getArgument(currentPathArg.replace("<", "").replace(">", ""));
						if (argument.getWords() > 1)
							a += argument.getWords() - 1;
					}

					if (p+1 == args.length) {
						if (dynamicArg) {
							RegisteredCommandArgument argument = command.getArgument(currentPathArg.replace("<", "").replace(">", ""));

							if (argument.getWords() != 1) continue command;

							CommandCompleter completer = getCompleter(argument.getCompleterClass());
							Collection<String> supplied = completer.complete(sender, input, currentGivenArg);

							if (supplied.isEmpty()) {
							} else if (argument.getRaw()) {
								suggestions.addAll(supplied);
							} else {
								List<String> list = supplied instanceof ArrayList ? (List<String>) supplied : new ArrayList<>(supplied);

								list.removeIf(current -> !current.toLowerCase().startsWith(currentGivenArg));
								Collections.sort(list);

								suggestions.addAll(list);
							}

						} else {
							if (!currentPathArg.isEmpty())
								suggestions.add(currentPathArg);
						}

						continue command; // with other alt names we will get the same output, so we can skip them
					}
					if (!currentGivenArg.equalsIgnoreCase(currentPathArg)) continue command; // incorrect path, skip command
				}

			}

		}

		List<String> list = new ArrayList<>(suggestions);
		Collections.sort(list);
		return list;
	}

	@Override
	public void executeCommand(@Nonnull CommandSender sender, @Nonnull String input) {
		String lowered = input.toLowerCase();

		Collection<RegisteredCommand> matchingName = new CopyOnWriteArraySet<>();
		command: for (RegisteredCommand command : commands) {
			if (!command.getScope().covers(sender)) continue;

			// if the command begins with the cloud prefix, remove it
			String stripped = lowered;
			if (command.getScope().hasCloudPrefix() && sender instanceof PlayerCommandSender) {
				if (lowered.startsWith("cloud"))
					stripped = lowered.substring("cloud".length());
				while (stripped.startsWith(" "))
					stripped = stripped.substring(1);
			}

			for (String name : command.getNames()) {
				boolean startsWithName = stripped.startsWith(name);
				boolean startsWithInput = name.startsWith(stripped);
				if (!startsWithName && !startsWithInput) continue;
				if (!startsWithName) {
					matchingName.add(command);
					continue;
				}

				// the current alt name of the command was used
				String remaining = stripped.substring(name.length());
				// check whether this name was really used: input: player, name: players -> incorrect
				if (!remaining.startsWith(" ") && !remaining.isEmpty()) continue;

				while (remaining.startsWith(" "))
					remaining = remaining.substring(1);

				String[] args = remaining.split(" ");

				String path = command.getPath();
				Map<String, String> argumentValues = new LinkedHashMap<>();
				String[] pathArgs = path.split(" ");
				for (int p = 0, a = 0; p < pathArgs.length; p++, a++) {
					// p: the current path args index
					// a: the current given args index

					String currentPathArg = pathArgs[p];
					boolean dynamicArg = currentPathArg.startsWith("<") && currentPathArg.endsWith(">");
					if (dynamicArg) {
						RegisteredCommandArgument argument = command.getArgument(currentPathArg.replace("<", "").replace(">", ""));
						if (args.length <= a + argument.getWords()) {
							if (!argument.getOptional()) continue command;
						}
						String[] valueArray = Arrays.copyOfRange(args, a, a + argument.getWords());
						String value = StringUtils.getArrayAsString(valueArray, " ");

						argumentValues.put(argument.getName(), value);

						if (argument.getWords() > 1)
							a += argument.getWords() - 1;

						if (args.length <= a) continue command;
					} else {
						if (args.length <= a) continue command;
						if (!currentPathArg.equalsIgnoreCase(args[a]))  // incorrect path, skip command
							continue command;
					}

					if (p+1 >= pathArgs.length) {
						execute(command, sender, argumentValues);
						return;
					}

				}

			}

		}

		if (matchingName.isEmpty()) {
			sender.sendTranslation("cloud.command.unknown");
			return;
		}

		for (RegisteredCommand command : matchingName) {
			sender.sendTranslation("cloud.command.syntax", command.getNames()[0] + " " + command.getPath());
		}
	}

	protected void execute(@Nonnull RegisteredCommand command, @Nonnull CommandSender sender, @Nonnull Map<String, String> argumentValues) {
		if (!command.getPermission().isEmpty() && !sender.hasPermission(command.getPermission())) {
			sender.sendTranslation("command.permission.needed");
			return;
		}

		Parameter[] methodsParameters = command.getMethod().getParameters();
		Object[] parameters = new Object[methodsParameters.length];
		for (int i = 0; i < methodsParameters.length; i++) {
			Parameter parameter = methodsParameters[i];
			if (CommandSender.class.isAssignableFrom(parameter.getType())) {
				parameters[i] = sender;
			} else if (parameter.isAnnotationPresent(CommandArgument.class)) {
				parameters[i] = argumentValues.get(parameter.getAnnotation(CommandArgument.class).value());
			}
		}

		try {
			command.getMethod().invoke(command.getInstance(), parameters);
		} catch (Throwable ex) {
			sender.sendTranslation("cloud.command.invoke.error");
			CloudDriver.getInstance().getLogger().error("An error occurred while handling command '{}'", command.getNames()[0], ex);
		}
	}

	@Nonnull
	@Override
	public CommandCompleter getCompleter(@Nonnull Class<? extends CommandCompleter> completerClass) {
		return completer.computeIfAbsent(completerClass, key -> {
			try {
				return completerClass.newInstance();
			} catch (Throwable ex) {
				throw new WrappedException("Could not create command completer instance of " + completerClass.getName(), ex);
			}
		});
	}
}
