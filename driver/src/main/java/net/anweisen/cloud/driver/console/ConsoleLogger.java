package net.anweisen.cloud.driver.console;

import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.logging.LogLevel;
import net.anweisen.utilities.common.logging.internal.FallbackLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ConsoleLogger implements ILogger {

	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private final Console console;

	private LogLevel level = LogLevel.TRACE;

	public ConsoleLogger(@Nonnull Console console, @Nonnull LogLevel level) {
		this.console = console;
		this.level = level;
	}

	public ConsoleLogger(@Nonnull Console console) {
		this.console = console;
	}

	@Override
	public void log(@Nonnull LogLevel level, @Nullable String message, @Nonnull Object... args) {
		if (!level.isShownAtLoggerLevel(this.level)) return;

		StringBuilder format = new StringBuilder()
			.append(ConsoleColor.DARK_GRAY)
			.append("[")
			.append(ConsoleColor.WHITE)
			.append(dateFormat.format(Date.from(Instant.now())))
			.append(" ");

		SpacePadder.leftPad(format, Thread.currentThread().getName(), 18);

		format.append(ConsoleColor.DARK_GRAY)
			.append("] ")
			.append(level.isColorized() ? ConsoleColor.RED : ConsoleColor.GRAY);

		SpacePadder.rightPad(format, level.getUpperCaseName() + ConsoleColor.DARK_GRAY + ":", 9 + (ConsoleColor.DARK_GRAY + ":").length());

		format.append(level.isColorized() ? ConsoleColor.YELLOW : ConsoleColor.DEFAULT)
			.append(FallbackLogger.formatMessage(message, args));


		Throwable ex = null;
		for (Object arg : args) {
			if (arg instanceof Throwable)
				ex = (Throwable) arg;
		}
		if (ex != null) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			format.append(System.lineSeparator()).append(writer);
		}

		console.writeLine(format.toString());
	}

	@Nonnull
	@Override
	public LogLevel getMinLevel() {
		return level;
	}

	@Nonnull
	@Override
	public ILogger setMinLevel(@Nonnull LogLevel level) {
		this.level = level;
		return this;
	}

	@Nullable
	@Override
	public String getName() {
		return null;
	}

}
