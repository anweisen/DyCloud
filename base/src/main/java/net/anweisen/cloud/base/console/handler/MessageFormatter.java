package net.anweisen.cloud.driver.console.handler;

import net.anweisen.cloud.driver.console.ConsoleColor;
import net.anweisen.cloud.driver.console.SpacePadder;
import net.anweisen.utilities.common.logging.handler.LogEntry;
import net.anweisen.utilities.common.logging.handler.LogHandler;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class MessageFormatter {

	@Nonnull
	public static String formatUncolored(@Nonnull LogEntry entry) {
		StringBuilder builder = new StringBuilder()
			.append("[")
			.append(LogHandler.TIME_FORMAT.format(Date.from(entry.getTimestamp())))
			.append(" ")
			.append(entry.getThreadName())
			.append("] ")
			.append(entry.getLevel().getUpperCaseName())
			.append(": ")
			.append(entry.getMessage());

		if (entry.getException() != null) {
			StringWriter writer = new StringWriter();
			entry.getException().printStackTrace(new PrintWriter(writer));
			builder.append(System.lineSeparator()).append(writer);
		}

		return builder.toString();
	}

	@Nonnull
	public static String formatColored(@Nonnull LogEntry entry) {
		StringBuilder builder = new StringBuilder()
			.append(ConsoleColor.DARK_GRAY)
			.append("[")
			.append(ConsoleColor.WHITE)
			.append(LogHandler.TIME_FORMAT.format(Date.from(entry.getTimestamp())))
			.append(" ");

		String name = entry.getThreadName();
		if (name.length() > 18) name = name.substring(name.length() - 18);
		SpacePadder.padLeft(builder, name, 18);

		builder.append(ConsoleColor.DARK_GRAY)
			.append("] ")
			.append(entry.getLevel().isHighlighted() ? ConsoleColor.RED : ConsoleColor.GRAY);

		SpacePadder.padRight(builder, entry.getLevel().getUpperCaseName() + ConsoleColor.DARK_GRAY + ":", 10 + ConsoleColor.DARK_GRAY.toString().length());
		builder.append(entry.getLevel().isHighlighted() ? ConsoleColor.YELLOW : ConsoleColor.DEFAULT)
			.append(entry.getMessage());

		if (entry.getException() != null) {
			StringWriter writer = new StringWriter();
			entry.getException().printStackTrace(new PrintWriter(writer));
			builder.append(System.lineSeparator()).append(writer);
		}

		return builder.toString();
	}

	private MessageFormatter() {}

}
