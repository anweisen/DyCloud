package net.anweisen.cloud.driver.database.remote;

import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.utilities.database.Database;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseManager implements DatabaseManager {

	private final Database database = new RemoteDatabase();

	@Nonnull
	@Override
	public Database getDatabase() {
		return database;
	}

}
