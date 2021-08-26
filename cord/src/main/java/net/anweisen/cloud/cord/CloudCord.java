package net.anweisen.cloud.cord;

import net.anweisen.cloud.cord.config.CordConfig;
import net.anweisen.cloud.cord.reporter.CordTrafficReporter;
import net.anweisen.cloud.cord.reporter.DefaultTrafficReporter;
import net.anweisen.cloud.cord.socket.NettyCordSocketServer;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.HeaderPrinter;
import net.anweisen.cloud.driver.cord.CordManager;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.network.HostAndPort;
import net.anweisen.cloud.driver.network.SocketClient;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.network.handler.SocketChannelClientHandler;
import net.anweisen.cloud.driver.network.listener.AuthenticationResponseListener;
import net.anweisen.cloud.driver.network.listener.PublishConfigListener;
import net.anweisen.cloud.driver.network.listener.ServiceInfoUpdateListener;
import net.anweisen.cloud.driver.network.netty.client.NettySocketClient;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket.AuthenticationType;
import net.anweisen.cloud.driver.node.NodeManager;
import net.anweisen.cloud.driver.player.PlayerManager;
import net.anweisen.cloud.driver.service.RemoteServiceManager;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.config.RemoteServiceConfigManager;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see DriverEnvironment#CORD
 */
public final class CloudCord extends CloudDriver {

	private final CordConfig config = new CordConfig();

	private final Console console;

	private final ServiceManager serviceManager;
	private final ServiceConfigManager serviceConfigManager;

	private SocketClient socketClient;
	private NettyCordSocketServer cordServer;
	private CordTrafficReporter trafficReporter;

	CloudCord(@Nonnull ILogger logger, @Nonnull Console console) {
		super(logger, DriverEnvironment.CORD);
		setInstance(this);

		this.console = console;

		serviceManager = new RemoteServiceManager();
		serviceConfigManager = new RemoteServiceConfigManager();

		HeaderPrinter.printHeader(console, this);
	}

	public synchronized void start() throws Exception {
		logger.info("Launching the CloudCord..");
		logger.extended("Localhost IP: {}", HostAndPort.localhost());

		logger.debug("Loading cord configuration..");
		config.load();
		console.setScreenName(getComponentName());

		socketClient = new NettySocketClient(SocketChannelClientHandler::new);

		loadNetworkListeners(socketClient.getListenerRegistry());
		connectAndAwaitAuthentication();

		try {
			startCord();
		} catch (Exception ex) {
			logger.error("Unable to start cord", ex);
			// TODO stop
		}

	}

	private void connectAndAwaitAuthentication() throws InterruptedException {

		AuthenticationResponseListener listener;
		Lock lock = new ReentrantLock();
		try {

			lock.lock();

			Condition condition = lock.newCondition();
			listener = new AuthenticationResponseListener(lock, condition);

			logger.info("Connecting to master socket on {}..", config.getMasterAddress());
			socketClient.getListenerRegistry().addListener(PacketConstants.AUTH_CHANNEL, listener);
			socketClient.connect(config.getMasterAddress());

			sendAuthentication();

			logger.debug("Waiting for authentication response..");
			condition.await();

		} finally {
			lock.unlock();
		}

		if (!listener.getResult())
			throw new IllegalStateException("Network authentication failed: " + listener.getMessage());

		logger.debug("Network authentication was successful");

	}

	private void sendAuthentication() {
		logger.debug("Sending authentication to master.. Cord: '{}'", config.getCordName());
		socketClient.sendPacket(new AuthenticationPacket(AuthenticationType.CORD, config.getIdentity(), config.getCordName(), buffer -> buffer.writeObject(config.getBindAddress())));
	}

	private void loadNetworkListeners(@Nonnull PacketListenerRegistry registry) {
		logger.info("Registering network listeners..");

		registry.addListener(PacketConstants.PUBLISH_CONFIG_CHANNEL, new PublishConfigListener());
		registry.addListener(PacketConstants.SERVICE_INFO_PUBLISH_CHANNEL, new ServiceInfoUpdateListener());
	}

	public synchronized void startCord() throws Exception {

		logger.info("Starting cord server on {}..", config.getBindAddress());
		trafficReporter = new DefaultTrafficReporter();
		cordServer = new NettyCordSocketServer();
		cordServer.init(config.getBindAddress());
		logger.info("Cord server listening on {}", config.getBindAddress());

		trafficReporter.start();

	}

	@Override
	public void shutdown() throws Exception {

		logger.info("Closing CordServer..");
		cordServer.close();

		console.close();

		shutdownDriver();

	}

	@Nonnull
	@Override
	public CordConfig getConfig() {
		return config;
	}

	@Nonnull
	public Console getConsole() {
		return console;
	}

	@Nonnull
	@Override
	public SocketClient getSocketComponent() {
		return socketClient;
	}

	@Nonnull
	@Override
	public DatabaseManager getDatabaseManager() {
		return null;
	}

	@Nonnull
	@Override
	public ServiceConfigManager getServiceConfigManager() {
		return serviceConfigManager;
	}

	@Nonnull
	@Override
	public ServiceFactory getServiceFactory() {
		return null;
	}

	@Nonnull
	@Override
	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	@Nonnull
	@Override
	public NodeManager getNodeManager() {
		return null;
	}

	@Nonnull
	@Override
	public CordManager getCordManager() {
		return null;
	}

	@Nonnull
	@Override
	public PlayerManager getPlayerManager() {
		return null;
	}

	@Nonnull
	public CordTrafficReporter getTrafficReporter() {
		return trafficReporter;
	}

	@Nonnull
	@Override
	public String getComponentName() {
		return config.getCordName();
	}

	private static CloudCord instance;

	public static CloudCord getInstance() {
		if (instance == null)
			instance = (CloudCord) CloudDriver.getInstance();

		return instance;
	}

}
