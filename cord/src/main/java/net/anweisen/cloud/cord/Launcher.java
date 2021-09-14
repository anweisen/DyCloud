package net.anweisen.cloud.cord;

import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.jline3.JLine3Console;
import net.anweisen.cloud.driver.console.handler.ConsoleLogHandler;
import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.logging.LogLevel;
import net.anweisen.utilities.common.logging.handler.HandledAsyncLogger;
import net.anweisen.utilities.common.logging.handler.HandledLogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class Launcher {

	public static void main(String[] args) throws Exception {
		Console console = new JLine3Console();
		HandledLogger logger = new HandledAsyncLogger(LogLevel.TRACE);
		init(console, logger);

		CloudCord cord = new CloudCord(logger, console);
		cord.start();
	}

	private static void init(@Nonnull Console console, @Nonnull HandledLogger logger) {
		logger.addHandler(new ConsoleLogHandler(console));

		ILogger.setConstantFactory(logger);

		System.setOut(logger.asPrintStream(LogLevel.INFO));
		System.setErr(logger.asPrintStream(LogLevel.ERROR));
	}
}
