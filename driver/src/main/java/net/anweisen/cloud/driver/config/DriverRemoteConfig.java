package net.anweisen.cloud.driver.config;

import net.anweisen.cloud.driver.network.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface DriverRemoteConfig extends DriverConfig {

	@Nonnull
	HostAndPort getMasterAddress();

}
