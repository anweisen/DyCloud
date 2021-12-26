package net.anweisen.cloud.modules.chattab.bukkit.handler;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface TabHandler {

	void update();

	@Nonnull
	TabFormatter getFormatter();

	void setFormatter(@Nonnull TabFormatter formatter);

}
