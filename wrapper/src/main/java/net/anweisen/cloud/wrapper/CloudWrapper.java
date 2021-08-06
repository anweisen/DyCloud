package net.anweisen.cloud.wrapper;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.network.SocketClient;
import net.anweisen.cloud.driver.network.handler.SocketChannelClientHandler;
import net.anweisen.cloud.driver.network.netty.client.NettySocketClient;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket.AuthenticationType;
import net.anweisen.cloud.driver.node.NodeManager;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.cloud.driver.service.specific.ServiceEnvironment;
import net.anweisen.cloud.wrapper.config.WrapperConfig;
import net.anweisen.cloud.wrapper.listeners.AuthenticationResponseListener;
import net.anweisen.utilities.common.logging.ILogger;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudWrapper extends CloudDriver {

	private final Path workDirectory = Paths.get("");

	private final WrapperConfig config = new WrapperConfig();

	private final Thread mainThread = Thread.currentThread();
	private Thread applicationThread;

	private ClassLoader applicationClassLoader;

	private SocketClient socketClient;

	public CloudWrapper(@Nonnull ILogger logger) {
		super(logger, DriverEnvironment.WRAPPER);
		setInstance(this);
	}

	public synchronized void start() throws Exception {
		logger.info("Launching the CloudWrapper..");

		socketClient = new NettySocketClient(SocketChannelClientHandler::new);

		connectAndAwaitAuthentication();

		try {
			logger.debug("Copying server configs..");
			copyConfig();
		} catch (Exception ex) {
			logger.error("Unable to copy server configs");
		}

		try {
			startApplication();
		} catch (Exception ex) {
			logger.error("Unable to start application", ex);
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
		logger.debug("Sending authentication to master.. Service: '{}'", config.getName());
		socketClient.sendPacket(new AuthenticationPacket(AuthenticationType.SERVICE, buffer -> {
			buffer.writeUUID(config.getIdentity()).writeString(config.getName());
		}));
	}

	private void copyConfig() throws IOException {

		ServiceEnvironment environment = config.getTask().getEnvironment();
		for (String config : environment.getConfigs()) {
			FileUtils.copy(
				new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("files/" + config)),
				new BufferedOutputStream(new FileOutputStream(Paths.get(config).toFile()))
			);
		}

	}

	public synchronized void startApplication() throws Exception {

		Path applicationFile = getApplicationFile();
		if (applicationFile == null) throw new IllegalStateException("Unable to locate application file");

		applicationClassLoader = new URLClassLoader(new URL[] { applicationFile.toFile().toURI().toURL() }, this.getClass().getClassLoader());

		String mainClassName = getMainClass(applicationFile);
		if (mainClassName == null) throw new IllegalStateException("Cannot extract main class from manifest");

		Class<?> mainClass = applicationClassLoader.loadClass(mainClassName);
		Method mainMethod = mainClass.getMethod("main", String[].class);

		applicationThread = new Thread(() -> {
			try {
				logger.info("Starting application thread..");
				mainMethod.invoke(
					null,
					new Object[] { new String[0] }
				);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}, "Application-Thread");

		applicationThread.setContextClassLoader(applicationClassLoader);
		applicationThread.start();

	}

	@Nullable
	private String getMainClass(@Nonnull Path applicationFile) {
		try (JarFile jarFile = new JarFile(applicationFile.toFile())) {
			Manifest manifest = jarFile.getManifest();
			return manifest == null ? null : manifest.getMainAttributes().getValue("Main-Class");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Nullable
	private Path getApplicationFile() throws IOException {
		return Files.list(workDirectory)
			.filter(path -> path.toString().endsWith(".jar"))
			.filter(path -> !path.toString().endsWith("wrapper.jar"))
			.findFirst().orElse(null);
	}

	@Override
	public void shutdown() throws Exception {

		shutdownDriver();

	}

	@Nonnull
	public ClassLoader getApplicationClassLoader() {
		return applicationClassLoader;
	}

	@Nonnull
	public Thread getApplicationThread() {
		return applicationThread;
	}

	@Nonnull
	public Thread getMainThread() {
		return mainThread;
	}

	@Nonnull
	public Path getWorkDirectory() {
		return workDirectory;
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
		return null;
	}

	@Nonnull
	@Override
	public ServiceFactory getServiceFactory() {
		return null;
	}

	@Nonnull
	@Override
	public ServiceManager getServiceManager() {
		return null;
	}

	@Nonnull
	@Override
	public NodeManager getNodeManager() {
		return null;
	}

	@Nonnull
	@Override
	public String getComponentName() {
		return config.getName();
	}
}
