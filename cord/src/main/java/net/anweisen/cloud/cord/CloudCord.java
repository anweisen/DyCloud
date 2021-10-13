package net.anweisen.cloud.cord;

import net.anweisen.cloud.cord.config.CordConfig;
import net.anweisen.cloud.cord.reporter.CordTrafficReporter;
import net.anweisen.cloud.cord.reporter.DefaultTrafficReporter;
import net.anweisen.cloud.cord.socket.NettyCordSocketServer;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudCord {

	private final CordConfig config = new CordConfig();

	private final ILogger logger = CloudDriver.getInstance().getLogger();

	private NettyCordSocketServer cordServer;
	private CordTrafficReporter trafficReporter;

	CloudCord() {
		instance = this;
	}

	public synchronized void start() throws Exception {
		logger.debug("Loading cord configuration..");
		config.load();

		try {
			startCord();
		} catch (Exception ex) {
			logger.error("Unable to start cord", ex);
			// TODO stop
		}

	}

	public synchronized void startCord() throws Exception {

		logger.info("Starting cord server on {}..", config.getBindAddress());
		trafficReporter = new DefaultTrafficReporter();
		cordServer = new NettyCordSocketServer();
		cordServer.init(config.getBindAddress());
		logger.info("Cord server listening on {}", config.getBindAddress());

		trafficReporter.start();

	}

	public void shutdown() throws Exception {

		logger.info("Closing CordServer..");
		cordServer.close();

	}

	@Nonnull
	public CordConfig getConfig() {
		return config;
	}

	@Nonnull
	public CordTrafficReporter getTrafficReporter() {
		return trafficReporter;
	}

	private static CloudCord instance;

	public static CloudCord getInstance() {
		return instance;
	}

}
