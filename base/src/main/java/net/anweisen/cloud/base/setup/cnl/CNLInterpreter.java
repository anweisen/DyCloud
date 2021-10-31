package net.anweisen.cloud.base.setup.cnl;

import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CNLInterpreter {

	private CNLInterpreter() {}

	private static final Map<String, CNLCommand> commands = new LinkedHashMap<>();

	public static void registerCommand(@Nonnull String name, @Nonnull CNLCommand command) {
		commands.put(name, command);
	}

	public static void withFile(@Nonnull Path path) throws IOException {
		withReader(FileUtils.newBufferedReader(path));
	}

	public static void withInputStream(@Nonnull InputStream input) throws IOException {
		withReader(new BufferedReader(new InputStreamReader(input)));
	}

	public static void withReader(@Nonnull BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			withLine(line);
		}
	}

	public static void withLine(@Nonnull String line) {
		if (line.startsWith("#") || line.trim().isEmpty()) return;

		String[] args = line.split(" ");
		String name = args[0];

		if (args.length == 1) {
			args = new String[0];
		} else {
			args = Arrays.copyOfRange(args, 1, args.length);
		}

		CNLCommand command = commands.get(name);
		command.executeCommand(args, String.join(" ", args), line);
	}
}
