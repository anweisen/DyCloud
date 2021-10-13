package net.anweisen.cloud.driver.console.handler;

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
public final class UncoloredMessageFormatter {

	@Nonnull
	public static String format(@Nonnull LogEntry entry) {
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

	private UncoloredMessageFormatter() {}

}
