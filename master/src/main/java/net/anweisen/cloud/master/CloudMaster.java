package net.anweisen.cloud.master;

import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.network.request.RequestPacketListener;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.HeaderPrinter;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.network.HostAndPort;
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
import net.anweisen.cloud.master.network.listener.*;
import net.anweisen.cloud.master.network.requests.TemplateRequestHandlers;
import net.anweisen.cloud.master.node.DefaultNodeServerManager;
import net.anweisen.cloud.master.node.NodeServerManager;
import net.anweisen.cloud.master.service.MasterServiceFactory;
import net.anweisen.cloud.master.service.MasterServiceManager;
import net.anweisen.cloud.master.service.config.MasterServiceConfigManager;
import net.anweisen.utilities.common.collection.NamedThreadFactory;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see DriverEnvironment#MASTER
 */
public final class CloudMaster extends CloudBase {

	private final MasterConfig config = new MasterConfig();

	private final ScheduledExecutorService mainLoopExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(id -> "MainLoop"));
	private final CloudMainLoop mainLoop = new CloudMainLoop(this);

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
		logger.extended("Localhost IP: {}", HostAndPort.localhost());

		logger.info("Loading cloud configuration..");
		config.load();

		logger.info("Loading service configurations..");
		serviceConfigManager.loadTasks();
		serviceConfigManager.registerTemplateStorage(LocalTemplateStorage.createDefault());

		logger.info("Opening socket server on {}..", config.getHostAddress());
		socketServer = new NettySocketServer(SocketChannelServerHandler::new);
		socketServer.addListener(config.getHostAddress());

		loadNetworkListeners(socketServer.getListenerRegistry());

		moduleManager.setModulesDirectory(Paths.get("modules"));
		initModules();

		logger.info("Loading database..");
		databaseManager.loadDatabase();

		if (permissionManager != null) {
			logger.info("Initializing permission management..");
			permissionManager.reload();
		}

		enableModules();

		mainLoopExecutor.scheduleAtFixedRate(mainLoop, 1, 1, TimeUnit.SECONDS);
		logger.info("Started main loop execution");

		logger.info("The cloud master is ready and running!");

	}

	private void loadNetworkListeners(@Nonnull PacketListenerRegistry registry) {
		logger.info("Registering network listeners..");

		registry.addListener(PacketConstants.AUTH_CHANNEL, new AuthenticationListener(this));
		registry.addListener(PacketConstants.DATABASE_CHANNEL, new DatabaseActionListener(databaseManager));
		registry.addListener(PacketConstants.NODE_DATA_CYCLE, new NodeDataCycleListener());
		registry.addListener(PacketConstants.PLAYER_EVENT_CHANNEL, new PlayerEventListener());
		registry.addListener(PacketConstants.PLAYER_EXECUTOR_CHANNEL, new PlayerExecutorListener());
		registry.addListener(PacketConstants.PLAYER_REMOTE_MANAGER_CHANNEL, new PlayerRemoteManagerListener());
		registry.addListener(PacketConstants.MODULE_SYSTEM_CHANNEL, new ModuleSystemListener());
		registry.addListener(PacketConstants.REQUEST_API_CHANNEL, new RequestPacketListener(
			new TemplateRequestHandlers()
		));
	}

	private void initModules() {
		moduleManager.resolveModules();
		moduleManager.loadModules();
	}

	private void enableModules() {
		moduleManager.enableModules();
	}

	@Override
	public synchronized void shutdown() throws Exception {

		logger.info("Shutting down..");

		logger.info("Closing all socket channels..");
		socketServer.closeChannels();

		shutdownBase();
		shutdownDriver();

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
	public CloudServiceManager getServiceManager() {
		return serviceManager;
	}

	@Nonnull
	@Override
	public NodeServerManager getNodeManager() {
		return nodeManager;
	}

	@Nonnull
	@Override
	public CordServerManager getCordManager() {
		return cordManager;
	}

	@Nonnull
	@Override
	public MasterPlayerManager getPlayerManager() {
		return playerManager;
	}

	@Nonnull
	@Override
	public String getComponentName() {
		return "Master";
	}

	@Nonnull
	@Override
	public MasterConfig getConfig() {
		return config;
	}

	@Nonnull
	@Override
	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	private static CloudMaster instance;

	public static CloudMaster getInstance() {
		if (instance == null)
			instance = (CloudMaster) CloudDriver.getInstance();

		return instance;
	}

}
