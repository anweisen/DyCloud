package net.anweisen.cloud.master;

import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.network.request.RequestPacketListener;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.HeaderPrinter;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.network.SocketServer;
import net.anweisen.cloud.driver.network.netty.server.NettySocketServer;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.config.LocalTemplateStorage;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.cloud.master.config.MasterConfig;
import net.anweisen.cloud.master.database.MasterDatabaseManager;
import net.anweisen.cloud.master.network.handler.SocketChannelServerHandler;
import net.anweisen.cloud.master.network.listener.AuthenticationListener;
import net.anweisen.cloud.master.network.listener.DatabaseActionListener;
import net.anweisen.cloud.master.network.requests.TemplateRequestHandlers;
import net.anweisen.cloud.master.node.DefaultNodeServerManager;
import net.anweisen.cloud.master.node.NodeServerManager;
import net.anweisen.cloud.master.service.MasterServiceFactory;
import net.anweisen.cloud.master.service.MasterServiceManager;
import net.anweisen.cloud.master.service.config.MasterServiceConfigManager;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudMaster extends CloudBase {

	private final MasterConfig config = new MasterConfig();

	private final MasterDatabaseManager databaseManager;
	private final MasterServiceConfigManager serviceConfigManager;
	private final NodeServerManager nodeManager;
	private final MasterServiceManager serviceManager;
	private final ServiceFactory serviceFactory;

	private SocketServer socketServer;

	CloudMaster(@Nonnull ILogger logger, @Nonnull Console console) {
		super(logger, console, DriverEnvironment.MASTER);
		console.setScreenName(getComponentName());
		setInstance(this);

		nodeManager = new DefaultNodeServerManager();
		databaseManager = new MasterDatabaseManager();
		serviceConfigManager = new MasterServiceConfigManager();
		serviceManager = new MasterServiceManager();
		serviceFactory = new MasterServiceFactory(this);

		HeaderPrinter.printHeader(console, this);
	}

	public synchronized void start() throws Exception {
		logger.info("Launching the CloudMaster..");

		logger.info("Loading cloud configuration..");
		config.load();

		logger.info("Loading database..");
		databaseManager.loadDatabase();

		logger.info("Loading service configurations..");
		serviceConfigManager.loadTasks();
		serviceConfigManager.registerTemplateStorage(LocalTemplateStorage.createDefault());

		logger.info("Opening socket server on {}..", config.getHostAddress());
		socketServer = new NettySocketServer(SocketChannelServerHandler::new);
		socketServer.addListener(config.getHostAddress());

		loadNetworkListeners(socketServer.getListenerRegistry());
	}

	private void loadNetworkListeners(@Nonnull PacketListenerRegistry registry) {
		logger.info("Registering network listeners..");

		registry.addListener(PacketConstants.AUTH_CHANNEL, new AuthenticationListener(this));
		registry.addListener(PacketConstants.DATABASE_CHANNEL, new DatabaseActionListener(databaseManager));
		registry.addListener(PacketConstants.REQUEST_API_CHANNEL, new RequestPacketListener(
			new TemplateRequestHandlers()
		));
	}

	@Nonnull
	@Override
	public SocketServer getSocketComponent() {
		return socketServer;
	}

	@Nonnull
	@Override
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	@Nonnull
	@Override
	public ServiceConfigManager getServiceConfigManager() {
		return serviceConfigManager;
	}

	@Nonnull
	@Override
	public ServiceFactory getServiceFactory() {
		return serviceFactory;
	}

	@Nonnull
	@Override
	public MasterServiceManager getServiceManager() {
		return serviceManager;
	}

	@Nonnull
	@Override
	public NodeServerManager getNodeManager() {
		return nodeManager;
	}

	@Nonnull
	@Override
	public String getComponentName() {
		return "Master";
	}

	@Nonnull
	public MasterConfig getConfig() {
		return config;
	}

	private static CloudMaster instance;

	public static CloudMaster getInstance() {
		if (instance == null)
			instance = (CloudMaster) CloudDriver.getInstance();

		return instance;
	}

}
