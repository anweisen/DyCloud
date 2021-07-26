package net.anweisen.cloud.master;

import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.ConsoleLogger;
import net.anweisen.cloud.driver.console.JLine3Console;
import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.logging.LogLevel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class Launcher {

	public static void main(String[] args) throws Exception {
		Console console = new JLine3Console();
		ILogger logger = new ConsoleLogger(console);
		init(console, logger);

		CloudMaster cloud = new CloudMaster(logger, console);
		cloud.start();
	}

	private static void init(@Nonnull Console console, @Nonnull ILogger logger) {
		ILogger.setConstantFactory(logger);

		System.setOut(logger.asPrintStream(LogLevel.INFO));
		System.setErr(logger.asPrintStream(LogLevel.ERROR));
	}

}
