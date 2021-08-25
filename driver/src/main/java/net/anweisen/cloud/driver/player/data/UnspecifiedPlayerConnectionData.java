package net.anweisen.cloud.driver.player.data;

import net.anweisen.cloud.driver.network.HostAndPort;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see PlayerServerConnectionData
 * @see PlayerProxyConnectionData
 */
public interface UnspecifiedPlayerConnectionData {

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	String getName();

	@Nullable
	HostAndPort getAddress();

}
