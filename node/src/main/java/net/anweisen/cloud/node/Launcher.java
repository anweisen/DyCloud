package net.anweisen.cloud.node;

import net.anweisen.cloud.base.console.Console;
import net.anweisen.cloud.base.console.handler.ConsoleLogHandler;
import net.anweisen.cloud.base.console.jline3.JLine3Console;
import net.anweisen.cloud.base.setup.SetupRunner;
import net.anweisen.cloud.driver.console.handler.FileLogHandler;
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
		SetupRunner.runSetupJob();

		Console console = new JLine3Console();
		HandledLogger logger = new HandledAsyncLogger(LogLevel.fromName(System.getProperty("dycloud.logging.level", "INFO")));
		init(console, logger);

		CloudNode cloud = new CloudNode(logger, console);
		cloud.start();
	}

	private static void init(@Nonnull Console console, @Nonnull HandledLogger logger) {
		logger.addHandler(new ConsoleLogHandler(console), new FileLogHandler());

		ILogger.setConstantFactory(logger);

		System.setOut(logger.asPrintStream(LogLevel.INFO));
		System.setErr(logger.asPrintStream(LogLevel.ERROR));
	}

}
