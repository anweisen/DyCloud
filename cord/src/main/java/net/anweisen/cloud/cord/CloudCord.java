package net.anweisen.cloud.cord;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.network.SocketComponent;
import net.anweisen.cloud.driver.node.NodeManager;
import net.anweisen.cloud.driver.player.PlayerManager;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.ServiceManager;
import net.anweisen.cloud.driver.service.config.ServiceConfigManager;
import net.anweisen.utilities.common.logging.ILogger;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudCord extends CloudDriver {


	CloudCord(@Nonnull ILogger logger) {
		super(logger, DriverEnvironment.CORD);
	}

	@Override
	public void shutdown() throws Exception {

		shutdownDriver();

	}

	@Nonnull
	@Override
	public SocketComponent getSocketComponent() {
		return null;
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
	public PlayerManager getPlayerManager() {
		return null;
	}

	@Nonnull
	@Override
	public String getComponentName() {
		return null;
	}

}