package net.anweisen.cloud.node;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import net.anweisen.cloud.base.CloudBase;
import net.anweisen.cloud.base.network.request.RequestPacketListener;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.HeaderPrinter;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.database.remote.RemoteDatabaseManager;
import net.anweisen.cloud.driver.network.SocketClient;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.network.handler.SocketChannelClientHandler;
import net.anweisen.cloud.driver.network.netty.client.NettySocketClient;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.node.NodeManager;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.cloud.node.config.NodeConfig;
import net.anweisen.cloud.node.listeners.AuthenticationResponseListener;
import net.anweisen.cloud.node.listeners.PublishConfigListener;
import net.anweisen.cloud.node.network.requests.ServiceFactoryRequestHandlers;
import net.anweisen.cloud.node.node.NodeNodeManager;
import net.anweisen.cloud.node.service.NodeServiceFactory;
import net.anweisen.cloud.node.service.NodeServiceManager;
import net.anweisen.cloud.node.service.config.NodeServiceConfigManager;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudNode extends CloudBase {

	private final NodeConfig config = new NodeConfig();

	private final DatabaseManager databaseManager;
	private final ServiceConfigManager serviceConfigManager;
	private final ServiceManager serviceManager;
	private final NodeServiceFactory serviceFactory;
	private final NodeManager nodeManager;

	private SocketClient socketClient;
	private DockerClient dockerClient;

	CloudNode(@Nonnull ILogger logger, @Nonnull Console console) {
		super(logger, console, DriverEnvironment.NODE);
		setInstance(this);

		Path wrapperOrigin = Paths.get("wrapper.jar");
		if (!Files.exists(wrapperOrigin)) throw new IllegalStateException("Missing wrapper.jar");

		nodeManager = new NodeNodeManager();
		databaseManager = new RemoteDatabaseManager();
		serviceConfigManager = new NodeServiceConfigManager();
		serviceManager = new NodeServiceManager();
		serviceFactory = new NodeServiceFactory();

		HeaderPrinter.printHeader(console, this);
	}

	public synchronized void start() throws Exception {
		logger.info("Launching the CloudNode..");

		logger.info("Loading cloud configuration..");
		config.load();
		console.setScreenName(getComponentName());

		logger.info("Connecting to docker daemon..");
		initDocker();

		logger.info("Connecting to master socket on {}..", config.getMasterAddress());
		socketClient = new NettySocketClient(SocketChannelClientHandler::new);

		loadNetworkListeners(socketClient.getListenerRegistry());
		connectAndAwaitAuthentication();

	}

	private void initDocker() {

		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
			.withDockerHost(this.config.getDockerHost())
			.build();

		DockerHttpClient httpClient = new JerseyDockerHttpClient.Builder()
			.dockerHost(config.getDockerHost())
			.sslConfig(config.getSSLConfig())
			.build();

		dockerClient = DockerClientBuilder.getInstance(config)
			.withDockerHttpClient(httpClient)
			.build();

		// Test connection
		dockerClient.pingCmd().exec();
		logger.debug("Successfully pinged docker daemon");

	}

	private void connectAndAwaitAuthentication() throws Exception {

		AuthenticationResponseListener listener;
		Lock lock = new ReentrantLock();
		try {

			lock.lock();

			Condition condition = lock.newCondition();
			listener = new AuthenticationResponseListener(lock, condition);

			socketClient.getListenerRegistry().addListener(PacketConstants.AUTH_CHANNEL, listener);
			socketClient.connect(config.getMasterAddress());

			sendAuthentication();

			logger.info("Waiting for authentication response..");
			condition.await();

		} finally {
			lock.unlock();
		}

		if (!listener.getResult())
			throw new IllegalStateException("Network authentication failed: " + listener.getMessage());

		logger.info("Network authentication was successful");

	}

	private void loadNetworkListeners(@Nonnull PacketListenerRegistry registry) {
		logger.info("Registering network listeners..");

		registry.addListener(PacketConstants.PUBLISH_CONFIG_CHANNEL, new PublishConfigListener(this));
		registry.addListener(PacketConstants.REQUEST_API_CHANNEL, new RequestPacketListener(
			new ServiceFactoryRequestHandlers()
		));
	}

	private void sendAuthentication() {
		logger.info("Sending authentication to master..");
		socketClient.sendPacket(new Packet(
				PacketConstants.AUTH_CHANNEL,
				Buffer.create().writeString(config.getNodeName()).writeUUID(config.getIdentity())
			)
		);
	}

	@Nonnull
	@Override
	public SocketClient getSocketComponent() {
		return socketClient;
	}

	@Nonnull
	public DockerClient getDockerClient() {
		return dockerClient;
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
	public NodeServiceFactory getServiceFactory() {
		return serviceFactory;
	}

	@Nonnull
	@Override
	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	@Nonnull
	@Override
	public NodeManager getNodeManager() {
		return nodeManager;
	}

	@Nonnull
	@Override
	public String getComponentName() {
		return config.getNodeName();
	}

	@Nonnull
	public NodeConfig getConfig() {
		return config;
	}

	private static CloudNode instance;

	public static CloudNode getInstance() {
		if (instance == null)
			instance = (CloudNode) CloudDriver.getInstance();

		return instance;
	}

}
