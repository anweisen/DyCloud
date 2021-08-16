package net.anweisen.cloud.wrapper;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.database.remote.RemoteDatabaseManager;
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
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

	private final DatabaseManager databaseManager = new RemoteDatabaseManager();
	private final List<String> commandlineArguments;
	private final Instrumentation instrumentation;


	private final Thread mainThread = Thread.currentThread();
	private Thread applicationThread;

	private ClassLoader applicationClassLoader;

	private SocketClient socketClient;

	CloudWrapper(@Nonnull ILogger logger, @Nonnull List<String> commandlineArguments, @Nonnull Instrumentation instrumentation) {
		super(logger, DriverEnvironment.WRAPPER);
		setInstance(this);

		this.commandlineArguments = commandlineArguments;
		this.instrumentation = instrumentation;
	}

	public synchronized void start() throws Exception {
		logger.info("Launching the CloudWrapper..");

		socketClient = new NettySocketClient(SocketChannelClientHandler::new);

		loadNetworkListeners(socketClient.getListenerRegistry());
		connectAndAwaitAuthentication();

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

	private void loadNetworkListeners(@Nonnull PacketListenerRegistry registry) {
		logger.info("Registering network listeners..");

		registry.addListener(PacketConstants.PUBLISH_CONFIG_CHANNEL, new PublishConfigListener());
		registry.addListener(PacketConstants.SERVICE_INFO_PUBLISH_CHANNEL, new ServiceInfoUpdateListener());
	}

	private void sendAuthentication() {
		logger.debug("Sending authentication to master.. Service: '{}'", config.getName());
		socketClient.sendPacket(new AuthenticationPacket(AuthenticationType.SERVICE, buffer -> {
			buffer.writeUUID(config.getIdentity()).writeString(config.getName());
		}));
	}

	public synchronized void startApplication() throws Exception {

		String applicationFileName = commandlineArguments.remove(0);
		logger.debug("Using '{}' as application file..", applicationFileName);

		Path applicationFile = Paths.get(applicationFileName);
		if (Files.notExists(applicationFile)) throw new IllegalStateException("Application file " + applicationFileName + " does not exist");
		URL applicationFileUrl = applicationFile.toUri().toURL();

		// Inject the classpath into current class loader
		// This eliminates the need of creating a new class loader and setting it as the system class loader which does not work properly with java16
		// TODO just use the instrumentation?
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			Class<?> loaderClass = loader.getClass();
			Field ucpField = ReflectionUtils.getInheritedPrivateField(loaderClass, "ucp");
			ucpField.setAccessible(true);
			Object ucp = ucpField.get(loader);

			Class<?> ucpClass = ucp.getClass();
			Method addUrlMethod = ucpClass.getDeclaredMethod("addURL", URL.class);
			addUrlMethod.setAccessible(true);
			addUrlMethod.invoke(ucp, applicationFileUrl);

			applicationClassLoader = loader;
		} catch (Throwable exInjectClassPath) {
			logger.error("Unable to inject application file to ucp (class path) of current class loader. Fallbacking to an URLClassLoader.", exInjectClassPath);
			applicationClassLoader = new URLClassLoader(new URL[] { applicationFileUrl }, this.getClass().getClassLoader());

			// https://stackoverflow.com/questions/5380275/replacement-system-classloader-for-classes-in-jars-containing-jars
			// Replace system class loader with created URLCLassLoader
			try {
				Field loaderField = ClassLoader.class.getDeclaredField("scl");
				loaderField.setAccessible(true);
				loaderField.set(null, applicationClassLoader);

				// field in pre java9 that indicated whether a system class loader is set
				Field setField = ClassLoader.class.getDeclaredField("sclSet");
				setField.setAccessible(true);
				setField.set(null, true);
			} catch (Throwable exReplaceSystemLoader) {
			}
		}



		String mainClassName = getMainClass(applicationFile);
		if (mainClassName == null) throw new IllegalStateException("Cannot extract main class from manifest");
		logger.debug("Using '{}' as main class..", mainClassName);

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

	}

	@Override
	public synchronized void shutdown() throws Exception {

		shutdownDriver();

	@Nonnull
	public Instrumentation getInstrumentation() {
		return instrumentation;
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
		return databaseManager;
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

	private static CloudWrapper instance;

	public static CloudWrapper getInstance() {
		if (instance == null)
			instance = (CloudWrapper) CloudDriver.getInstance();

		return instance;
	}
}
