package net.anweisen.cloud.modules.bridge.helper.data;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class MinecraftPlayerInfo {

	private String name;
	private UUID uuid;

	public MinecraftPlayerInfo(@Nonnull String name, @Nonnull UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public UUID getUniqueId() {
		return uuid;
	}
}
