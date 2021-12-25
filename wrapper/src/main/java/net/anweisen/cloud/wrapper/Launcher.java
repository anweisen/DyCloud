package net.anweisen.cloud.wrapper;

import net.anweisen.cloud.driver.console.handler.DefaultLogHandler;
import net.anweisen.utility.common.logging.ILogger;
import net.anweisen.utility.common.logging.LogLevel;
import net.anweisen.utility.common.logging.handler.HandledAsyncLogger;
import net.anweisen.utility.common.logging.handler.HandledLogger;

import javax.annotation.Nonnull;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class Launcher {

	private static Instrumentation instrumentationInstance;

	public static void premain(String premainArgs, Instrumentation instrumentation) {
		instrumentationInstance = instrumentation;
	}

	public static void agentmain(String agentArgs, Instrumentation instrumentation) {
		instrumentationInstance = instrumentation;
	}

	public static void main(String[] args) throws Exception {
		HandledLogger logger = new HandledAsyncLogger(LogLevel.TRACE);
		init(logger);

		CloudWrapper cloud = new CloudWrapper(logger, new ArrayList<>(Arrays.asList(args)), instrumentationInstance);
		cloud.start();
	}

	private static void init(@Nonnull HandledLogger logger) {
		logger.addHandler(new DefaultLogHandler());

		ILogger.setConstantFactory(logger);
	}

}
