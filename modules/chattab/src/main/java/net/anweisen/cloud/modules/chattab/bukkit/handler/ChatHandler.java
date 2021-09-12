package net.anweisen.cloud.modules.chattab.bukkit.handler;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface ChatHandler {

	@Nullable
	String format(@Nonnull Player player, @Nonnull String message);

}
