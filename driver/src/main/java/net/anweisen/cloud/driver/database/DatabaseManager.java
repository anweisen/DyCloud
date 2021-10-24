package net.anweisen.cloud.driver.database;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.utilities.database.Database;
import net.anweisen.utilities.database.SpecificDatabase;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getDatabaseManager()
 */
public interface DatabaseManager {

	@Nonnull
	Database getDatabase();

	void setDatabase(@Nonnull Database database);

	@Nonnull
	default SpecificDatabase getDatabase(@Nonnull String name) {
		return getDatabase().getSpecificDatabase(name);
	}

}
