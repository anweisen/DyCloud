package net.anweisen.cloud.driver.service.specific.data;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see net.anweisen.cloud.driver.service.specific.ServiceProperty#WORLDS
 */
public final class WorldInfo {

	private UUID uniqueId;
	private String name;
	private String difficulty;

	private WorldInfo() {
	}

	public WorldInfo(@Nonnull UUID uniqueId, @Nonnull String name, @Nonnull String difficulty) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.difficulty = difficulty;
	}

	@Nonnull
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public String getDifficulty() {
		return difficulty;
	}

	@Override
	public String toString() {
		return "WorldInfo[name=" + name + " difficulty=" + difficulty + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WorldInfo worldInfo = (WorldInfo) o;
		return Objects.equals(uniqueId, worldInfo.uniqueId) && Objects.equals(name, worldInfo.name) && Objects.equals(difficulty, worldInfo.difficulty);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, name, difficulty);
	}
}
