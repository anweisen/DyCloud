package net.anweisen.cloud.wrapper;

import net.anweisen.cloud.wrapper.console.DefaultLogger;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class Launcher {

	private static Instrumentation instrumentationInstance;

	public static void agentmain(String agentArgs, Instrumentation instrumentation) {
		instrumentationInstance = instrumentation;
	}

	public static void main(String[] args) throws Exception {
		ILogger logger = new DefaultLogger();
		init(logger);

		CloudWrapper cloud = new CloudWrapper(logger);
		cloud.start();
	}

	private static void init(@Nonnull ILogger logger) {
		ILogger.setConstantFactory(logger);
	}

}
