package net.anweisen.cloud.modules.bridge.helper.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ProxyPlayerInfo {

	private String name;
	private UUID uuid;
	private String server;

	public ProxyPlayerInfo(@Nonnull String name, @Nonnull UUID uuid, @Nullable String server) {
		this.name = name;
		this.uuid = uuid;
		this.server = server;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public UUID getUniqueId() {
		return uuid;
	}

	@Nullable
	public String getServer() {
		return server;
	}
}
