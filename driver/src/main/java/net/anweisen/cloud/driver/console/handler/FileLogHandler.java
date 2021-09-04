package net.anweisen.cloud.driver.console.handler;

import net.anweisen.utilities.common.logging.handler.LogEntry;
import net.anweisen.utilities.common.logging.handler.LogHandler;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class FileLogHandler implements LogHandler {

	private final Path directory;
	private final Path errorFile;
	private final Path outFile;

	public FileLogHandler() {
		directory = Paths.get("logs");
		FileUtils.createDirectory(directory);

		outFile   = selectOutput("cloud.%s.log");
		errorFile = selectOutput("error.%s.log");
	}

	private Path selectOutput(@Nonnull String pattern) {
		for (int i = 0; ; i++) {
			Path file = directory.resolve(String.format(pattern, i));

			if (Files.notExists(file))
				return file;
		}
	}

	@Override
	public void handle(@Nonnull LogEntry entry) throws Exception {
		if (entry.getLevel().isColorized()) {
			write(initStream(errorFile), entry);
		}
		write(initStream(outFile), entry);
	}

	@Nonnull
	private OutputStream initStream(@Nonnull Path file) throws IOException {
		if (!Files.exists(directory)) FileUtils.createDirectory(directory);
		return Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	private void write(@Nonnull OutputStream stream, @Nonnull LogEntry entry) throws Exception {
		stream.write((System.lineSeparator() + MessageFormatter.formatUncolored(entry)).getBytes(StandardCharsets.UTF_8));
		stream.flush();
		stream.close();
	}

}
