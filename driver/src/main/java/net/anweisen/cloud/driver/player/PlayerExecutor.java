package net.anweisen.cloud.driver.player;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerExecutor {

	@Nonnull
	UUID getPlayerUniqueId();

}
