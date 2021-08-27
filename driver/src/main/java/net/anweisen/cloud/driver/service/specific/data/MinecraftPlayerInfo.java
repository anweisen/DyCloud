package net.anweisen.cloud.driver.service.specific.data;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class MinecraftPlayerInfo {

	private String name;
	private UUID uuid;

	private MinecraftPlayerInfo() {
	}

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
