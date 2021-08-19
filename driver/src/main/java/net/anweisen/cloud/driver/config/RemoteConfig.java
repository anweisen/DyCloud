package net.anweisen.cloud.driver.config;

import net.anweisen.cloud.driver.network.HostAndPort;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface RemoteConfig extends DriverConfig {

	@Nonnull
	HostAndPort getMasterAddress();

}
