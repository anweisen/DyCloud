package net.anweisen.cloud.cord;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.utility.common.logging.ILogger;
import net.anweisen.utility.common.logging.LogLevel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class Launcher {

	public static void main(String[] args) throws Exception {
		init(CloudDriver.getInstance().getLogger());

		CloudCord cord = new CloudCord();
		cord.start();
	}

	private static void init(@Nonnull ILogger logger) {
		ILogger.setConstantFactory(logger);

		System.setOut(logger.asPrintStream(LogLevel.INFO));
		System.setErr(logger.asPrintStream(LogLevel.ERROR));
	}
}
