package net.anweisen.cloud.driver.service.specific.data;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#PLAYERS
 */
public final class PlayerInfo {

	private UUID uniqueId;
	private String name;

	private PlayerInfo() {
	}

	public PlayerInfo(@Nonnull UUID uniqueId, @Nonnull String name) {
		this.uniqueId = uniqueId;
		this.name = name;
	}

	@Nonnull
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ":" + uniqueId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayerInfo that = (PlayerInfo) o;
		return Objects.equals(uniqueId, that.uniqueId) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, name);
	}
}
