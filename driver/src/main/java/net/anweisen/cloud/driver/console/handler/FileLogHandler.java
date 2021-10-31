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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class FileLogHandler implements LogHandler {

	public static final long mbConversion = 1024 * 1024;

	private static final String fileExtension = System.getProperty("dycloud.logging.file.extension", ".log");
	private static final long maxBytes = Long.parseLong(System.getProperty("dycloud.logging.file.size", "16")) * mbConversion;
	private static final Path directory = Paths.get("logs");

	private final AtomicReference<Path> errorFile = new AtomicReference<>();
	private final AtomicReference<Path> outFile = new AtomicReference<>();

	public FileLogHandler() {
		FileUtils.createDirectory(directory);
	}

	@Nonnull
	private Path selectFile(@Nonnull String prefix) {
		List<Path> files = FileUtils.list(directory)
			.sorted(Comparator.comparingInt(file -> logIndex(file.getFileName(), prefix)))
			.filter(path -> path.getFileName().toString().startsWith(prefix))
			.collect(Collectors.toList());

		while (files.size() >= 5) {
			Path file = files.remove(0);
			FileUtils.deleteFile(file);
		}

		int index = 0;
		if (!files.isEmpty())
			index = logIndex(files.get(files.size() - 1).getFileName(), prefix) + 1;

		return directory.resolve(prefix + index + fileExtension);
	}

	private int logIndex(@Nonnull Path file, @Nonnull String prefix) {
		try {
			return Integer.parseInt(file.toString().replace(prefix, "").replace(fileExtension, ""));
		} catch (Exception ex) {
			return -1;
		}
	}

	@Override
	public void handle(@Nonnull LogEntry entry) throws Exception {
		if (entry.getLevel().isHighlighted())
			write(initStream(errorFile, () -> selectFile("error.")), entry);
		write(initStream(outFile, () -> selectFile("cloud.")), entry);
	}

	@Nonnull
	private OutputStream initStream(@Nonnull AtomicReference<Path> file, @Nonnull Supplier<Path> selectNewFile) throws IOException {
		if (!Files.exists(directory)) FileUtils.createDirectory(directory);
		if (file.get() == null || Files.size(file.get()) > maxBytes) file.set(selectNewFile.get());
		if (!Files.exists(file.get())) FileUtils.createFile(file.get());
		return Files.newOutputStream(file.get(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	private void write(@Nonnull OutputStream stream, @Nonnull LogEntry entry) throws Exception {
		stream.write((UncoloredMessageFormatter.format(entry) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
		stream.flush();
		stream.close();
	}

}
