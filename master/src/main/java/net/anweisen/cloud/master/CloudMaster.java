package net.anweisen.cloud.master;

import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.command.CommandManager;
import net.anweisen.cloud.base.console.Console;
import net.anweisen.cloud.base.console.HeaderPrinter;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.config.global.GlobalConfig;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.network.SocketServer;
import net.anweisen.cloud.driver.network.http.HttpServer;
import net.anweisen.cloud.driver.network.netty.http.NettyHttpServer;
import net.anweisen.cloud.driver.network.netty.server.NettySocketServer;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;
import net.anweisen.cloud.driver.network.packet.def.NodePublishPacket;
import net.anweisen.cloud.driver.network.packet.def.NodePublishPacket.NodePublishPayload;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.config.LocalTemplateStorage;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.cloud.driver.translate.TranslationManager;
import net.anweisen.cloud.master.command.MasterCommandManager;
import net.anweisen.cloud.master.config.MasterConfig;
import net.anweisen.cloud.master.config.global.MasterGlobalConfig;
import net.anweisen.cloud.master.database.MasterDatabaseManager;
import net.anweisen.cloud.master.loop.CloudMainLoop;
import net.anweisen.cloud.master.network.handler.SocketChannelServerHandler;
import net.anweisen.cloud.master.network.listener.*;
import net.anweisen.cloud.master.node.DefaultNodeServerManager;
import net.anweisen.cloud.master.node.NodeServerManager;
import net.anweisen.cloud.master.player.MasterPlayerManager;
import net.anweisen.cloud.master.service.CloudServiceManager;
import net.anweisen.cloud.master.service.MasterServiceFactory;
import net.anweisen.cloud.master.service.MasterServiceManager;
import net.anweisen.cloud.master.service.config.MasterServiceConfigManager;
import net.anweisen.cloud.master.translate.MasterTranslationManager;
import net.anweisen.utility.common.collection.NamedThreadFactory;
import net.anweisen.utility.common.logging.handler.HandledLogger;

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
	private final MasterPlayerManager playerManager;
	private final MasterTranslationManager translationManager;
	private final NodeServerManager nodeManager;
	private final CloudServiceManager serviceManager;
	private final ServiceFactory serviceFactory;
	private final GlobalConfig globalConfig;
	private final CommandManager commandManager;

	private SocketServer socketServer;
	private HttpServer httpServer;

	CloudMaster(@Nonnull HandledLogger logger, @Nonnull Console console) {
		super(logger, console, DriverEnvironment.MASTER);
		console.setScreenName(getComponentName());
		setInstance(this);

		nodeManager = new DefaultNodeServerManager();
		databaseManager = new MasterDatabaseManager();
		serviceConfigManager = new MasterServiceConfigManager();
		serviceManager = new MasterServiceManager();
		serviceFactory = new MasterServiceFactory();
		playerManager = new MasterPlayerManager();
		globalConfig = new MasterGlobalConfig();
		commandManager = new MasterCommandManager();
		translationManager = new MasterTranslationManager();

		HeaderPrinter.printHeader(console);
	}

	public synchronized void start() throws Exception {
		logger.info("Launching the CloudMaster..");
		logger.extended("Localhost IP: {}", HostAndPort.localhost());

		logger.info("Loading cloud configuration..");
		config.load();

		logger.info("Loading translations..");
		translationManager.setup();
		translationManager.retrieve();

		logger.info("Loading service configurations..");
		serviceConfigManager.loadTasks();
		serviceConfigManager.registerTemplateStorage(LocalTemplateStorage.createDefault());

		httpServer = new NettyHttpServer(config.getWebSslConfig());
		for (HostAndPort address : config.getHttpListeners()) {
			logger.info("Starting http server on {}..", address);
			httpServer.addListener(address);
		}

		logger.info("Opening socket server on {}..", config.getHostAddress());
		socketServer = new NettySocketServer(SocketChannelServerHandler::new);
		socketServer.addListener(config.getHostAddress());

		logger.info("Loading global config..");
		globalConfig.fetch();

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

		runConsole();
		registerDefaultCommands();

		logger.info("The cloud master is ready and running!");

	}

	private void loadNetworkListeners(@Nonnull PacketListenerRegistry registry) {
		logger.info("Registering network listeners..");

		registry.addListener(PacketChannels.AUTH_CHANNEL, new AuthenticationListener());
		registry.addListener(PacketChannels.DATABASE_CHANNEL, new RemoteDatabaseActionListener(databaseManager));
		registry.addListener(PacketChannels.NODE_DATA_CYCLE, new NodeDataCycleListener());
		registry.addListener(PacketChannels.SERVICE_INFO_PUBLISH_CHANNEL, new ServiceInfoUpdateListener());
		registry.addListener(PacketChannels.SERVICE_UPDATE_SELF_INFO_CHANNEL, new ServiceUpdateSelfInfoListener());
		registry.addListener(PacketChannels.SERVICE_CONTROL_CHANNEL, new ServiceControlListener());
		registry.addListener(PacketChannels.PLAYER_EVENT_CHANNEL, new PlayerEventListener());
		registry.addListener(PacketChannels.PLAYER_EXECUTOR_CHANNEL, new PlayerExecutorListener());
		registry.addListener(PacketChannels.PLAYER_REMOTE_MANAGER_CHANNEL, new PlayerRemoteManagerListener());
		registry.addListener(PacketChannels.MODULE_SYSTEM_CHANNEL, new ModuleSystemListener());
		registry.addListener(PacketChannels.COMMAND_SYSTEM_CHANNEL, new CommandSystemListener());
		registry.addListener(PacketChannels.GLOBAL_CONFIG_CHANNEL, new GlobalConfigListener());
		registry.addListener(PacketChannels.TEMPLATE_STORAGE_CHANNEL, new TemplateStorageListener());
		registry.addListener(PacketChannels.TRANSLATION_SYSTEM_CHANNEL, new TranslationSystemListener());
	}

	private void initModules() {
		moduleManager.resolveModules();
		moduleManager.loadModules();
	}

	private void enableModules() {
		moduleManager.enableModules();
	}

	@Override
	public void reload() throws Exception {
		logger.info("Reloading..");

		logger.info("Loading translations..");
		translationManager.retrieve();

		if (permissionManager != null) {
			logger.info("Reloading permission management..");
			permissionManager.reload();
		}

		// TODO send
		logger.info("Reloading modules..");
		moduleManager.disableModules();
		moduleManager.unregisterModules();
		moduleManager.resolveModules();
		moduleManager.loadModules();
		moduleManager.enableModules();

		logger.info("Reloading complete");
	}

	@Override
	public synchronized void shutdown() throws Exception {

		logger.info("Shutting down..");
		mainLoopExecutor.shutdownNow();

		logger.info("Closing all socket channels..");
		socketServer.closeChannels();
		logger.info("Shutting down socket server..");
		socketServer.shutdown();

		logger.info("Shutting down http server..");
		httpServer.shutdown();

		shutdownBase();
		shutdownDriver();

	}

	public void publishUpdate(@Nonnull NodePublishPayload payload, @Nonnull NodeInfo nodeInfo) {
		getSocketComponent().sendPacketSync(new NodePublishPacket(payload, nodeInfo));
	}

	@Nonnull
	@Override
	public SocketServer getSocketComponent() {
		return socketServer;
	}

	@Nonnull
	public HttpServer getHttpServer() {
		return httpServer;
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
	public CommandManager getCommandManager() {
		return commandManager;
	}

	@Nonnull
	@Override
	public MasterPlayerManager getPlayerManager() {
		return playerManager;
	}

	@Nonnull
	@Override
	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	@Nonnull
	@Override
	public TranslationManager getTranslationManager() {
		return translationManager;
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

	private static CloudMaster instance;

	public static CloudMaster getInstance() {
		if (instance == null)
			instance = (CloudMaster) CloudDriver.getInstance();

		return instance;
	}

}
