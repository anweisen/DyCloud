package net.anweisen.cloud.driver.config;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface DriverConfig {

	void load();

	@Nonnull
	UUID getIdentity();

}
