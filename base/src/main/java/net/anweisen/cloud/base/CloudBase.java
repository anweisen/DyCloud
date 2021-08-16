package net.anweisen.cloud.base;

import net.anweisen.cloud.base.module.DefaultModuleManager;
import net.anweisen.cloud.base.module.ModuleManager;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.PublishType;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CloudBase extends CloudDriver {

	protected final ModuleManager moduleManager = new DefaultModuleManager();

	protected final Console console;

	public CloudBase(@Nonnull ILogger logger, @Nonnull Console console, @Nonnull DriverEnvironment environment) {
		super(logger, environment);
		this.console = console;
	}

	protected final void shutdownBase() throws Exception {

		console.close();

	}

	public void publishUpdate(@Nonnull PublishType publishType, @Nonnull ServiceInfo serviceInfo, @Nonnull SocketChannel... skipChannels) {
		getSocketComponent().sendPacket(new ServiceInfoPublishPacket(publishType, serviceInfo), skipChannels);
	}

	@Nonnull
	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	private static CloudBase instance;

	public static CloudBase getInstance() {
		if (instance == null)
			instance = (CloudBase) CloudDriver.getInstance();

		return instance;
	}
}
