package net.anweisen.cloud.driver.config;

import net.anweisen.cloud.driver.CloudDriver;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getConfig()
 */
public interface DriverConfig {

	void load() throws IOException;

	@Nonnull
	UUID getIdentity();

}
