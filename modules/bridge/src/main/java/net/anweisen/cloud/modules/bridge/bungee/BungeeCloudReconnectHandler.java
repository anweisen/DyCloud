package net.anweisen.cloud.modules.bridge.bungee;

import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BungeeCloudReconnectHandler implements ReconnectHandler {

	@Override
	@Nullable
	public ServerInfo getServer(@Nonnull ProxiedPlayer player) {
		return BungeeBridgeHelper.getNextFallback(player);
	}

	@Override
	public void setServer(@Nonnull ProxiedPlayer player) {
	}

	@Override
	public void save() {
	}

	@Override
	public void close() {
	}

}
