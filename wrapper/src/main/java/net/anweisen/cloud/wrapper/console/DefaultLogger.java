package net.anweisen.cloud.wrapper.console;

import net.anweisen.cloud.driver.console.SpacePadder;
import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.logging.LogLevel;
import net.anweisen.utilities.common.logging.internal.FallbackLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintStream;
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
public class DefaultLogger implements ILogger {

	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

	private LogLevel level = LogLevel.TRACE;

	@Override
	public void log(@Nonnull LogLevel level, @Nullable String message, @Nonnull Object... args) {
		if (!level.isShownAtLoggerLevel(this.level)) return;

		StringBuilder format = new StringBuilder()
			.append("[")
			.append(dateFormat.format(Date.from(Instant.now())))
			.append(" ");
		SpacePadder.leftPad(format, Thread.currentThread().getName(), 18);
		format.append("] ");
		SpacePadder.rightPad(format, level.getUpperCaseName() + ":", 9);
		format.append(FallbackLogger.formatMessage(message, args));

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

		PrintStream stream = level.isColorized() ? System.err : System.out;
		stream.println(format);
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
