package net.anweisen.cloud.driver.database;

import net.anweisen.utilities.database.Database;
import net.anweisen.utilities.database.SpecificDatabase;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface DatabaseManager {

	@Nonnull
	Database getDatabase();

	@Nonnull
	default SpecificDatabase getDatabase(@Nonnull String name) {
		return getDatabase().getSpecificDatabase(name);
	}

}
